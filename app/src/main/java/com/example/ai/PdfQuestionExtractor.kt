package com.example.ai

import com.example.data.model.QuestionEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import java.util.regex.Pattern

data class ExtractedQuestionDraft(
    val tempId: Long = System.nanoTime(),
    var questionText: String,
    var optionA: String,
    var optionB: String,
    var optionC: String,
    var optionD: String,
    var correctAnswer: String, // "A", "B", "C", "D"
    var explanation: String = "",
    var subjectId: Long = 18,
    var subjectName: String = "राजस्थान की कला एवं संस्कृति",
    var topicId: Long = 1801,
    var topicName: String = "राजस्थान के लोकदेवता",
    var examId: Long = 4,
    var examName: String = "Rajasthan 3rd Grade Teacher",
    var examYear: Int = 2024,
    var difficulty: String = "Medium",
    var questionNumber: Int = 1,
    var isDuplicate: Boolean = false,
    var duplicateReason: String = "",
    var isAnswerVerified: Boolean = true
)

object PdfQuestionExtractor {

    /**
     * Parses raw document text extracted from PDF or pasted by Admin.
     */
    fun parseTextToQuestions(
        rawText: String,
        defaultExamName: String = "Rajasthan Teacher Exams",
        defaultExamYear: Int = 2024,
        allSubjects: List<SubjectEntity> = emptyList(),
        allTopics: List<TopicEntity> = emptyList()
    ): List<ExtractedQuestionDraft> {
        val questions = mutableListOf<ExtractedQuestionDraft>()
        if (rawText.isBlank()) return questions

        // Normalize line breaks
        val normalized = rawText.replace("\r\n", "\n").replace("\r", "\n")

        // Split text by Question markers like:
        // Q1., Q.1, Q 1., 1., प्र. 1., प्रश्न 1:, प्रश्न 1., Q1 -
        val questionRegex = "(?m)(?:^|\\n)(?:Q\\.?\\s*\\d+|प्र(?:श्न)?\\.?\\s*\\d+|\\d+)[\\.\\:\\)\\-]\\s*"
        val matcher = Pattern.compile(questionRegex).matcher(normalized)

        val splitIndices = mutableListOf<Int>()
        while (matcher.find()) {
            splitIndices.add(matcher.start())
        }

        if (splitIndices.isEmpty()) {
            // Fallback: Attempt single question parsing if user pasted one item
            val single = parseSingleQuestionBlock(normalized, 1, defaultExamName, defaultExamYear, allSubjects, allTopics)
            if (single != null) questions.add(single)
            return questions
        }

        for (i in splitIndices.indices) {
            val start = splitIndices[i]
            val end = if (i + 1 < splitIndices.size) splitIndices[i + 1] else normalized.length
            val block = normalized.substring(start, end).trim()
            val parsed = parseSingleQuestionBlock(block, i + 1, defaultExamName, defaultExamYear, allSubjects, allTopics)
            if (parsed != null) {
                questions.add(parsed)
            }
        }

        return questions
    }

    private fun parseSingleQuestionBlock(
        block: String,
        index: Int,
        defaultExamName: String,
        defaultExamYear: Int,
        allSubjects: List<SubjectEntity>,
        allTopics: List<TopicEntity>
    ): ExtractedQuestionDraft? {
        val lines = block.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return null

        var qText = ""
        var optA = ""
        var optB = ""
        var optC = ""
        var optD = ""
        var ans = "A"
        var explanation = ""
        var answerFound = false

        // Extract explanation first if present
        var content = block
        val expPattern = Pattern.compile("(?i)(?:व्याख्या|Explanation|विवरण)\\s*[:\\-]([\\s\\S]+)$")
        val expMatcher = expPattern.matcher(content)
        if (expMatcher.find()) {
            explanation = expMatcher.group(1)?.trim() ?: ""
            content = content.substring(0, expMatcher.start()).trim()
        }

        // Extract Answer if present
        val ansPattern = Pattern.compile("(?i)(?:सही उत्तर|उत्तर|Ans|Answer|Key)\\s*[:\\-\\=]\\s*\\(?([A-D1-4a-dक-घ])\\)?")
        val ansMatcher = ansPattern.matcher(content)
        if (ansMatcher.find()) {
            val foundAns = ansMatcher.group(1)?.uppercase() ?: "A"
            ans = when (foundAns) {
                "1", "क", "A" -> "A"
                "2", "ख", "B" -> "B"
                "3", "ग", "C" -> "C"
                "4", "घ", "D" -> "D"
                else -> "A"
            }
            answerFound = true
            content = content.substring(0, ansMatcher.start()).trim()
        }

        // Extract Options A, B, C, D
        // Patterns: (A), A., A), (1), 1., (क), क.
        val optPattern = Pattern.compile("(?m)(?:^|\\s)(?:\\(?([A-D1-4क-घ])\\)?[\\.\\-\\s])\\s*(.+)")
        val optMatcher = optPattern.matcher(content)
        val foundOptions = mutableMapOf<String, String>()

        val optPositions = mutableListOf<Triple<Int, String, String>>()
        while (optMatcher.find()) {
            val keyRaw = optMatcher.group(1) ?: ""
            val key = when (keyRaw.uppercase()) {
                "1", "क", "A" -> "A"
                "2", "ख", "B" -> "B"
                "3", "ग", "C" -> "C"
                "4", "घ", "D" -> "D"
                else -> "A"
            }
            val text = optMatcher.group(2)?.trim() ?: ""
            optPositions.add(Triple(optMatcher.start(), key, text))
        }

        if (optPositions.size >= 4) {
            val firstOptPos = optPositions[0].first
            qText = content.substring(0, firstOptPos).trim()
            for (p in optPositions) {
                foundOptions[p.second] = p.third
            }
            optA = foundOptions["A"] ?: "विकल्प A"
            optB = foundOptions["B"] ?: "विकल्प B"
            optC = foundOptions["C"] ?: "विकल्प C"
            optD = foundOptions["D"] ?: "विकल्प D"
        } else {
            // Fallback line-by-line inspection
            val nonOptLines = mutableListOf<String>()
            for (line in content.lines()) {
                val l = line.trim()
                when {
                    l.startsWith("A.") || l.startsWith("(A)") || l.startsWith("A)") -> optA = l.substring(2).trim(' ', '.', ')')
                    l.startsWith("B.") || l.startsWith("(B)") || l.startsWith("B)") -> optB = l.substring(2).trim(' ', '.', ')')
                    l.startsWith("C.") || l.startsWith("(C)") || l.startsWith("C)") -> optC = l.substring(2).trim(' ', '.', ')')
                    l.startsWith("D.") || l.startsWith("(D)") || l.startsWith("D)") -> optD = l.substring(2).trim(' ', '.', ')')
                    else -> nonOptLines.add(l)
                }
            }
            qText = nonOptLines.joinToString(" ")
        }

        // Clean question title prefix
        qText = qText.replace(Regex("^(?:Q\\.?\\s*\\d+|प्र(?:श्न)?\\.?\\s*\\d+|\\d+)[\\.\\:\\)\\-]\\s*"), "").trim()

        if (qText.isBlank()) return null
        if (optA.isBlank()) optA = "विकल्प A"
        if (optB.isBlank()) optB = "विकल्प B"
        if (optC.isBlank()) optC = "विकल्प C"
        if (optD.isBlank()) optD = "विकल्प D"

        // Automatic AI Subject & Topic Classification
        val classification = classifyQuestionAi(qText, allSubjects, allTopics)

        return ExtractedQuestionDraft(
            tempId = System.currentTimeMillis() + index,
            questionText = qText,
            optionA = optA,
            optionB = optB,
            optionC = optC,
            optionD = optD,
            correctAnswer = ans,
            explanation = explanation,
            subjectId = classification.subjectId,
            subjectName = classification.subjectName,
            topicId = classification.topicId,
            topicName = classification.topicName,
            examId = 4,
            examName = defaultExamName,
            examYear = defaultExamYear,
            difficulty = "Medium",
            questionNumber = index,
            isDuplicate = false,
            duplicateReason = "",
            isAnswerVerified = answerFound
        )
    }

    data class AiClassificationResult(
        val subjectId: Long,
        val subjectName: String,
        val topicId: Long,
        val topicName: String
    )

    /**
     * AI-based classifier mapping Rajasthan syllabus keywords to subject and topic IDs
     */
    fun classifyQuestionAi(
        questionText: String,
        allSubjects: List<SubjectEntity>,
        allTopics: List<TopicEntity>
    ): AiClassificationResult {
        val q = questionText.lowercase()

        return when {
            // Folk Deities (लोकदेवता)
            q.contains("लोकदेवता") || q.contains("रामदेवजी") || q.contains("गोगाजी") ||
            q.contains("तेजाजी") || q.contains("पाबूजी") || q.contains("हड़बूजी") ||
            q.contains("मेहाजी") || q.contains("देवनारायण") || q.contains("मल्लिनाथ") ||
            q.contains("पंचपीर") || q.contains("रुणीचा") || q.contains("गोगामेड़ी") || q.contains("लीलण") ->
                AiClassificationResult(18, "राजस्थान की कला एवं संस्कृति", 1801, "राजस्थान के लोकदेवता")

            // Folk Goddesses (लोकदेवियाँ)
            q.contains("लोकदेवी") || q.contains("करणी माता") || q.contains("जीण माता") ||
            q.contains("शीला देवी") || q.contains("कैला देवी") || q.contains("सच्चियाय") ||
            q.contains("बाण माता") || q.contains("आई माता") || q.contains("ज्वाला माता") ->
                AiClassificationResult(18, "राजस्थान की कला एवं संस्कृति", 1802, "राजस्थान की लोकदेवियाँ")

            // Forts (दुर्ग)
            q.contains("दुर्ग") || q.contains("किला") || q.contains("चित्तौड़गढ़") ||
            q.contains("कुम्भलगढ़") || q.contains("मेहरानगढ़") || q.contains("रणथम्भौर") ||
            q.contains("गागरोण") || q.contains("सोनारगढ़") || q.contains("तारागढ़") ||
            q.contains("जूनागढ़") || q.contains("जल दुर्ग") || q.contains("कटारगढ़") ->
                AiClassificationResult(18, "राजस्थान की कला एवं संस्कृति", 1803, "राजस्थान के प्रमुख दुर्ग एवं किले")

            // Folk Dances & Arts
            q.contains("नृत्य") || q.contains("घूमर") || q.contains("कालबेलिया") ||
            q.contains("तेरहताली") || q.contains("गैर") || q.contains("चरी") || q.contains("अग्नि नृत्य") ->
                AiClassificationResult(18, "राजस्थान की कला एवं संस्कृति", 1810, "लोक नृत्य")

            // Paintings & Handicrafts
            q.contains("चित्रकला") || q.contains("बणी-ठणी") || q.contains("निहालचंद") ||
            q.contains("मीनाकारी") || q.contains("ब्लू पॉटरी") || q.contains("उस्ता कला") ||
            q.contains("थेवा कला") || q.contains("कृपाल सिंह") ->
                AiClassificationResult(18, "राजस्थान की कला एवं संस्कृति", 1809, "राजस्थान की हस्तकलाएँ")

            // Panchayati Raj & Administration
            q.contains("पंचायती राज") || q.contains("ग्राम सभा") || q.contains("सरपंच") ||
            q.contains("73वां संविधान") || q.contains("नागौर") || q.contains("बगदरी") ||
            q.contains("बलवंत राय") || q.contains("जिला परिषद") ->
                AiClassificationResult(19, "राजस्थान की राजव्यवस्था एवं प्रशासन", 1910, "पंचायती राज")

            // Polity & Commissions
            q.contains("राज्यपाल") || q.contains("मुख्यमंत्री") || q.contains("विधानसभा") ||
            q.contains("rpsc") || q.contains("लोक सेवा आयोग") || q.contains("मानवाधिकार") ||
            q.contains("उच्च न्यायालय") || q.contains("मुख्य सचिव") || q.contains("लोकायुक्त") ||
            q.contains("rti") || q.contains("सूचना का अधिकार") ->
                AiClassificationResult(19, "राजस्थान की राजव्यवस्था एवं प्रशासन", 1901, "राज्यपाल")

            // 1857 Revolution & Integration
            q.contains("1857") || q.contains("क्रांति") || q.contains("नसीराबाद") ||
            q.contains("आउवा") || q.contains("कुशाल सिंह") || q.contains("एरिनपुरा") ||
            q.contains("प्रजामंडल") || q.contains("बिजोलिया") || q.contains("किसान आंदोलन") ||
            q.contains("एकीकरण") || q.contains("मत्स्य संघ") || q.contains("विजय सिंह पथिक") ->
                AiClassificationResult(17, "राजस्थान का आधुनिक इतिहास एवं स्वतंत्रता आंदोलन", 1704, "राजस्थान का एकीकरण")

            // Ancient Civilizations & Dynasties
            q.contains("कालीबंगा") || q.contains("आहड़") || q.contains("बैराठ") ||
            q.contains("गणेश्वर") || q.contains("हल्दीघाटी") || q.contains("महाराणा प्रताप") ||
            q.contains("राणा सांगा") || q.contains("राणा कुंभा") || q.contains("हम्मीर") ||
            q.contains("पृथ्वीराज चौहान") || q.contains("गुर्जर प्रतिहार") ->
                AiClassificationResult(16, "राजस्थान का प्राचीन एवं मध्यकालीन इतिहास", 1605, "मेवाड़ का गुहिल व सिसोदिया राजवंश")

            // Geography: Rivers, Lakes, Climate, Soils
            q.contains("अरावली") || q.contains("गुरुशिखर") || q.contains("चम्बल") ||
            q.contains("बनास") || q.contains("लूनी") || q.contains("माही") ||
            q.contains("सांभर") || q.contains("पिछोला") || q.contains("बीसलपुर") ||
            q.contains("ignp") || q.contains("इंदिरा गांधी नहर") || q.contains("मावठ") ||
            q.contains("खेजड़ी") || q.contains("रेडक्लिफ") || q.contains("क्षेत्रफल") ->
                AiClassificationResult(2, "राजस्थान का भूगोल", 201, "राजस्थान: स्थिति, विस्तार एवं सीमाएँ")

            else -> {
                // Fallback matching against passed list of subjects if any
                val foundSub = allSubjects.find { s -> q.contains(s.nameHindi.take(6)) }
                if (foundSub != null) {
                    val foundTop = allTopics.find { t -> t.subjectId == foundSub.id }
                    AiClassificationResult(foundSub.id, foundSub.nameHindi, foundTop?.id ?: 1801, foundTop?.nameHindi ?: "सामान्य")
                } else {
                    AiClassificationResult(18, "राजस्थान की कला एवं संस्कृति", 1801, "राजस्थान के लोकदेवता")
                }
            }
        }
    }

    /**
     * Checks if a question draft duplicates any existing approved question
     */
    fun checkDuplicate(
        draft: ExtractedQuestionDraft,
        existingQuestions: List<QuestionEntity>
    ): Pair<Boolean, String> {
        val normalizedDraft = draft.questionText.trim().lowercase().replace(Regex("[\\s\\p{Punct}]+"), " ")

        for (exist in existingQuestions) {
            val normalizedExist = exist.questionText.trim().lowercase().replace(Regex("[\\s\\p{Punct}]+"), " ")
            if (normalizedDraft == normalizedExist) {
                return true to "समान प्रश्न पहले से प्रश्न बैंक (ID #${exist.id}) में मौजूद है।"
            }

            // Word overlap check for near-duplicate
            val wordsDraft = normalizedDraft.split(" ").filter { it.length > 3 }.toSet()
            val wordsExist = normalizedExist.split(" ").filter { it.length > 3 }.toSet()
            if (wordsDraft.isNotEmpty() && wordsExist.isNotEmpty()) {
                val intersection = wordsDraft.intersect(wordsExist).size
                val union = wordsDraft.union(wordsExist).size
                val jaccard = intersection.toFloat() / union.toFloat()
                if (jaccard > 0.75f) {
                    return true to "मिलता-जुलता प्रश्न मौजूद है (ID #${exist.id}): '${exist.questionText.take(40)}...'"
                }
            }
        }
        return false to ""
    }
}
