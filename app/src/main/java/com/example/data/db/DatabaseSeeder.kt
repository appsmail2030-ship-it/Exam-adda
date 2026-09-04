package com.example.data.db

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedInitialData(database: AppDatabase) = withContext(Dispatchers.IO) {
        val subjectDao = database.subjectDao()
        if (subjectDao.getSubjectsCount() > 0) {
            return@withContext // Database already seeded
        }

        val examDao = database.examDao()
        val topicDao = database.topicDao()
        val questionDao = database.questionDao()
        val testDao = database.testDao()
        val studyDao = database.studyDao()

        // 1. Exam Categories
        val exams = listOf(
            ExamCategoryEntity(1, "Rajasthan Teacher Exams", "राजस्थान शिक्षक भर्ती", true, 1),
            ExamCategoryEntity(2, "Rajasthan 1st Grade Teacher", "1st ग्रेड स्कूल व्याख्याता", true, 2),
            ExamCategoryEntity(3, "Rajasthan 2nd Grade Teacher", "2nd ग्रेड वरिष्ठ अध्यापक", true, 3),
            ExamCategoryEntity(4, "Rajasthan 3rd Grade Teacher", "3rd ग्रेड शिक्षक भर्ती", true, 4),
            ExamCategoryEntity(5, "REET", "रीट (REET)", true, 5),
            ExamCategoryEntity(6, "Rajasthan CET", "राजस्थान CET (समान पात्रता)", true, 6),
            ExamCategoryEntity(7, "Rajasthan Police", "राजस्थान पुलिस (कांस्टेबल/SI)", true, 7),
            ExamCategoryEntity(8, "Rajasthan Patwari", "राजस्थान पटवारी", true, 8),
            ExamCategoryEntity(9, "Rajasthan LDC", "राजस्थान LDC / कनिष्ठ सहायक", true, 9),
            ExamCategoryEntity(10, "Other Rajasthan Government Exams", "अन्य राजस्थान भर्ती परीक्षाएँ", false, 10)
        )
        examDao.insertExams(exams)

        // 2. Main Subjects (A to S)
        val subjects = listOf(
            SubjectEntity(1, "SUB_A", "राजस्थान सामान्य परिचय", "Rajasthan General Introduction", "info", 1),
            SubjectEntity(2, "SUB_B", "राजस्थान का भूगोल", "Rajasthan Geography", "map", 2),
            SubjectEntity(3, "SUB_C", "राजस्थान की जलवायु", "Rajasthan Climate", "thermostat", 3),
            SubjectEntity(4, "SUB_D", "राजस्थान की मिट्टी, अपरदन एवं मरुस्थलीकरण", "Soils, Erosion & Desertification", "terrain", 4),
            SubjectEntity(5, "SUB_E", "राजस्थान में सूखा, अकाल एवं आपदा", "Drought, Famine & Disaster", "warning", 5),
            SubjectEntity(6, "SUB_F", "राजस्थान की वन संपदा एवं पर्यावरण", "Forest & Environment", "forest", 6),
            SubjectEntity(7, "SUB_G", "जैव विविधता, वन्यजीव एवं अभयारण्य", "Wildlife & Sanctuaries", "pets", 7),
            SubjectEntity(8, "SUB_H", "राजस्थान में पशुपालन", "Animal Husbandry", "cruelty_free", 8),
            SubjectEntity(9, "SUB_I", "राजस्थान में कृषि एवं प्रमुख फसलें", "Agriculture & Crops", "agriculture", 9),
            SubjectEntity(10, "SUB_J", "राजस्थान में अपवाह एवं नदियाँ", "Drainage & Rivers", "water", 10),
            SubjectEntity(11, "SUB_K", "राजस्थान की झीलें एवं बावड़ियाँ", "Lakes & Stepwells", "pool", 11),
            SubjectEntity(12, "SUB_L", "प्रमुख बाँध, सिंचाई परियोजनाएँ एवं जल संसाधन", "Dams & Irrigation Projects", "water_damage", 12),
            SubjectEntity(13, "SUB_M", "राजस्थान की नहरें", "Canals (IGNP)", "linear_scale", 13),
            SubjectEntity(14, "SUB_N", "राजस्थान के उद्योग", "Industries of Rajasthan", "factory", 14),
            SubjectEntity(15, "SUB_O", "राजस्थान के खनिज संसाधन", "Mineral Resources", "diamond", 15),
            SubjectEntity(16, "SUB_P", "राजस्थान का प्राचीन एवं मध्यकालीन इतिहास", "Ancient & Medieval History", "history_edu", 16),
            SubjectEntity(17, "SUB_Q", "राजस्थान का आधुनिक इतिहास एवं स्वतंत्रता आंदोलन", "Modern History & Freedom Movement", "flag", 17),
            SubjectEntity(18, "SUB_R", "राजस्थान की कला एवं संस्कृति", "Art & Culture of Rajasthan", "palette", 18),
            SubjectEntity(19, "SUB_S", "राजस्थान की राजव्यवस्था एवं प्रशासन", "Polity & Administration", "account_balance", 19)
        )
        subjectDao.insertSubjects(subjects)

        // 3. Topics for Rajasthan Polity & Administration (Subject 19)
        val polityTopics = listOf(
            TopicEntity(1901, 19, "राज्यपाल", "Governor", 1),
            TopicEntity(1902, 19, "मुख्यमंत्री एवं राज्य मंत्रिपरिषद", "Chief Minister & Council of Ministers", 2),
            TopicEntity(1903, 19, "राज्य विधानसभा", "State Legislative Assembly", 3),
            TopicEntity(1904, 19, "राजस्थान विधानसभा की प्रक्रिया एवं नियम", "Assembly Rules & Procedure", 4),
            TopicEntity(1905, 19, "संसद में राजस्थान", "Rajasthan in Parliament", 5),
            TopicEntity(1906, 19, "उच्च न्यायालय एवं अधीनस्थ न्यायालय", "High Court & Subordinate Courts", 6),
            TopicEntity(1907, 19, "राज्य सचिवालय एवं मुख्य सचिव", "State Secretariat & Chief Secretary", 7),
            TopicEntity(1908, 19, "संभाग एवं जिला प्रशासन व्यवस्था", "Divisional & District Administration", 8),
            TopicEntity(1909, 19, "राजस्थान में पुलिस प्रशासन", "Police Administration", 9),
            TopicEntity(1910, 19, "पंचायती राज", "Panchayati Raj", 10),
            TopicEntity(1911, 19, "नगरीय स्वशासन", "Urban Local Governance", 11),
            TopicEntity(1912, 19, "प्रमुख आयोग (RPSC, मानवाधिकार, निर्वाचन)", "Key Commissions", 12),
            TopicEntity(1913, 19, "प्रमुख अधिनियम (लोक सेवा गारंटी, RTI 2005)", "Key Acts & RTI", 13),
            TopicEntity(1914, 19, "सामाजिक अंकेक्षण", "Social Audit", 14),
            TopicEntity(1915, 19, "जन सूचना एवं राजस्थान संपर्क पोर्टल", "Jan Soochna & Sampark Portal", 15)
        )
        topicDao.insertTopics(polityTopics)

        // Topics for Rajasthan Geography (Subject 2 & 1)
        val geoTopics = listOf(
            TopicEntity(201, 2, "राजस्थान: स्थिति, विस्तार एवं सीमाएँ", "Location, Extent & Boundaries", 1),
            TopicEntity(202, 2, "राजस्थान का भौतिक विभाजन (अरावली, मरुस्थल, मैदान, पठार)", "Physical Divisions", 2),
            TopicEntity(203, 2, "प्राचीन नाम एवं भौगोलिक उपनाम", "Ancient Names & Sobriquets", 3),
            TopicEntity(204, 2, "राजस्थान में जल संरक्षण एवं जल प्रबंधन", "Water Conservation", 4),
            TopicEntity(205, 2, "राजस्थान की जलवायु एवं वर्षा", "Climate & Rainfall", 5),
            TopicEntity(206, 2, "मिट्टी, अपरदन एवं मरुस्थलीकरण", "Soils & Desertification", 6),
            TopicEntity(207, 2, "वन संपदा, जैव विविधता एवं अभयारण्य", "Forests & Sanctuaries", 7),
            TopicEntity(208, 2, "पशुपालन एवं डेयरी विकास", "Animal Husbandry", 8),
            TopicEntity(209, 2, "कृषि एवं प्रमुख फसलें", "Agriculture & Crops", 9),
            TopicEntity(210, 2, "अपवाह तंत्र एवं नदियाँ (चम्बल, बनास, लूनी)", "Rivers System", 10),
            TopicEntity(211, 2, "राजस्थान की झीलें एवं प्रमुख बावड़ियाँ", "Lakes & Stepwells", 11),
            TopicEntity(212, 2, "प्रमुख बाँध एवं सिंचाई परियोजनाएँ (बीसलपुर, IGNP)", "Dams & Canals", 12),
            TopicEntity(213, 2, "राजस्थान के खनिज संसाधन एवं उद्योग", "Minerals & Industries", 13)
        )
        topicDao.insertTopics(geoTopics)

        // Topics for Rajasthan History (Subject 16 & 17)
        val historyTopics = listOf(
            TopicEntity(1601, 16, "राजस्थान के इतिहास के स्रोत (शिलालेख, सिक्के, ताम्रपत्र)", "Sources of History", 1),
            TopicEntity(1602, 16, "प्रमुख प्राचीन सभ्यताएँ (कालीबंगा, आहड़, बैराठ, गणेश्वर)", "Ancient Civilizations", 2),
            TopicEntity(1603, 16, "गुर्जर प्रतिहार वंश", "Gurjar Pratihar Dynasty", 3),
            TopicEntity(1604, 16, "चौहान राजवंश (अजमेर, रणथम्भौर, जालौर)", "Chauhan Dynasty", 4),
            TopicEntity(1605, 16, "मेवाड़ का गुहिल व सिसोदिया राजवंश (कुंभा, सांगा, प्रताप)", "Mewar Dynasty", 5),
            TopicEntity(1606, 16, "आमेर का कछवाहा राजवंश (मानसिंह, मिर्जा राजा, सवाई जयसिंह)", "Kachwaha of Amer", 6),
            TopicEntity(1607, 16, "मारवाड़ का राठौड़ राजवंश (मालदेव, चन्द्रसेन, जसवंत सिंह)", "Rathore of Marwar", 7),
            TopicEntity(1608, 16, "बीकानेर का राठौड़ राजवंश (रायसिंह, अनूपसिंह)", "Bikaner Rathore", 8),
            TopicEntity(1609, 16, "भरतपुर का जाट राजवंश (सूरजमल)", "Jat Dynasty", 9),
            TopicEntity(1701, 17, "राजस्थान में 1857 की क्रांति (नसीराबाद, आउवा, कोटा)", "1857 Revolution", 10),
            TopicEntity(1702, 17, "जनजातीय एवं किसान आंदोलन (बिजोलिया, बेगूं, गोविंद गिरी)", "Peasant & Tribal Movements", 11),
            TopicEntity(1703, 17, "प्रजामंडल आंदोलन एवं राजनीतिक जनजागरण", "Prajamandal Movement", 12),
            TopicEntity(1704, 17, "राजस्थान का एकीकरण (सात चरण)", "Integration of Rajasthan", 13),
            TopicEntity(1705, 17, "प्रमुख स्वतंत्रता सेनानी एवं ऐतिहासिक व्यक्तित्व", "Freedom Fighters", 14)
        )
        topicDao.insertTopics(historyTopics)

        // Topics for Rajasthan Art & Culture (Subject 18)
        val artCultureTopics = listOf(
            TopicEntity(1801, 18, "राजस्थान के लोकदेवता (रामदेवजी, गोगाजी, तेजाजी, पाबूजी)", "Folk Deities (Lok Devta)", 1),
            TopicEntity(1802, 18, "राजस्थान की लोकदेवियाँ (करणी माता, जीण माता, शीला देवी)", "Lok Deviyan", 2),
            TopicEntity(1803, 18, "राजस्थान के प्रमुख दुर्ग एवं किले (चित्तौड़गढ़, कुम्भलगढ़, मेहरानगढ़)", "Forts of Rajasthan", 3),
            TopicEntity(1804, 18, "महल, हवेलियाँ, छतरियाँ एवं जल स्थापत्य", "Palaces, Havelis & Cenotaphs", 4),
            TopicEntity(1805, 18, "राजस्थान के सम्प्रदाय एवं संत (दादू, जाम्भोजी, जसनाथजी, मीरा)", "Saints & Sects", 5),
            TopicEntity(1806, 18, "प्रमुख मंदिर, मस्जिदें, दरगाह एवं धार्मिक स्थल", "Temples & Dargahs", 6),
            TopicEntity(1807, 18, "राजस्थान के मेले एवं त्योहार", "Fairs & Festivals", 7),
            TopicEntity(1808, 18, "राजस्थान की चित्रकला शैलियाँ (मेवाड़, मारवाड़, ढूंढाड़, हाड़ौती)", "Paintings of Rajasthan", 8),
            TopicEntity(1809, 18, "राजस्थान की हस्तकलाएँ (मीनाकारी, उस्ता कला, थेवा कला, ब्लू पॉटरी)", "Handicrafts", 9),
            TopicEntity(1810, 18, "लोक नृत्य (घूमर, कालबेलिया, गैर, चरी, अग्नि नृत्य)", "Folk Dances", 10),
            TopicEntity(1811, 18, "लोक नाट्य (ख्याल, रम्मत, तमाशा, चारबेंत, गवरी)", "Folk Theatres", 11),
            TopicEntity(1812, 18, "राजस्थान के लोक संगीत एवं वाद्य यंत्र (रावणहत्था, कामायचा, अलगोजा)", "Music & Instruments", 12),
            TopicEntity(1813, 18, "राजस्थान के आभूषण, वेशभूषा एवं पहनावा", "Ornaments & Costumes", 13),
            TopicEntity(1814, 18, "राजस्थान की जनजातियाँ (भील, मीणा, गरासिया, सहरिया)", "Tribes of Rajasthan", 14),
            TopicEntity(1815, 18, "राजस्थानी भाषा, बोलियाँ एवं प्रमुख साहित्य", "Language, Dialects & Literature", 15)
        )
        topicDao.insertTopics(artCultureTopics)

        // Seed comprehensive questions for Lokdevta, Forts, Polity, Geography, History, Integration, etc.
        val questions = mutableListOf<QuestionEntity>()

        // 1. Topic: राजस्थान के लोकदेवता (Topic ID: 1801)
        questions.addAll(listOf(
            QuestionEntity(
                id = 1,
                questionText = "गोगाजी का प्रमुख मेला कहाँ आयोजित होता है?",
                optionA = "गोगामेड़ी (हनुमानगढ़)",
                optionB = "ददरेवा (चूरू)",
                optionC = "कोलू मण्ड (जोधपुर)",
                optionD = "रूणेचा (जैसलमेर)",
                correctAnswer = "A",
                explanation = "गोगाजी का मुख्य मेला भाद्रपद कृष्ण नवमी (गोगा नवमी) को गोगामेड़ी, नोहर (हनुमानगढ़) में भरता है। इनका जन्म स्थल ददरेवा (चूरू) है जिसे 'शीर्ष मेड़ी' तथा गोगामेड़ी को 'धुर मेड़ी' कहा जाता है।",
                subjectId = 18,
                topicId = 1801,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 1
            ),
            QuestionEntity(
                id = 2,
                questionText = "राजस्थान के किस लोकदेवता को 'पीरों का पीर' कहा जाता है?",
                optionA = "तेजाजी",
                optionB = "रामदेवजी",
                optionC = "पाबूजी",
                optionD = "हड़बूजी",
                correctAnswer = "B",
                explanation = "मक्का से आए पंच पीरों ने बाबा रामदेवजी के चमत्कार देखकर कहा था- 'म्हे तो केवल पीर हाँ, पर थे पीराँ रा पीर हो'। इसलिए बाबा रामदेवजी को 'पीरों का पीर' एवं 'रुणीचा रा धणी' कहा जाता है।",
                subjectId = 18,
                topicId = 1801,
                examId = 5,
                examName = "REET Level 2",
                examYear = 2023,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 2
            ),
            QuestionEntity(
                id = 3,
                questionText = "ऊंटों के देवता एवं प्लेग रक्षक देवता के रूप में कौन प्रसिद्ध हैं?",
                optionA = "पाबूजी",
                optionB = "तेजाजी",
                optionC = "देवनारायणजी",
                optionD = "मेहाजी मांगलिया",
                correctAnswer = "A",
                explanation = "मारवाड़ में सर्वप्रथम ऊंट (सांडे) लाने का श्रेय पाबूजी को जाता है। ऊंट बीमार होने पर रेबारी (राइका) जाति द्वारा पाबूजी की फड़ का वाचन रावणहत्था वाद्य यंत्र के साथ किया जाता है।",
                subjectId = 18,
                topicId = 1801,
                examId = 3,
                examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2022,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 3
            ),
            QuestionEntity(
                id = 4,
                questionText = "तेजाजी की घोड़ी का नाम क्या था?",
                optionA = "केसर कालमी",
                optionB = "लीलण (शिंगारी)",
                optionC = "नीली घोड़ी",
                optionD = "किरण काबरा",
                correctAnswer = "B",
                explanation = "वीर तेजाजी की घोड़ी का नाम लीलण (शिंगारी) था। केसर कालमी घोड़ी पाबूजी की थी जिसे देवल चारणी ने दिया था।",
                subjectId = 18,
                topicId = 1801,
                examId = 6,
                examName = "Rajasthan CET",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 4
            ),
            QuestionEntity(
                id = 5,
                questionText = "कामड़िया पंथ की स्थापना किस लोकदेवता ने की थी?",
                optionA = "रामदेवजी",
                optionB = "मल्लिनाथजी",
                optionC = "तल्लीनाथजी",
                optionD = "कल्लाजी राठौड़",
                correctAnswer = "A",
                explanation = "सामाजिक समरसता व छुआछूत निवारण हेतु बाबा रामदेवजी ने कामड़िया पंथ चलाया। इस पंथ की महिलाओं द्वारा रामदेवजी के मेले में प्रसिद्ध 'तेरहताली नृत्य' मंजीरों के साथ किया जाता है।",
                subjectId = 18,
                topicId = 1801,
                examId = 7,
                examName = "Rajasthan Police Constable",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 5
            ),
            QuestionEntity(
                id = 6,
                questionText = "चार हाथों वाले लोकदेवता के रूप में किन्हें पूजा जाता है?",
                optionA = "वीर कल्लाजी राठौड़",
                optionB = "वीर फत्ताजी",
                optionC = "झूंझारजी",
                optionD = "केसरिया कुंवरजी",
                correctAnswer = "A",
                explanation = "चित्तौड़गढ़ के तीसरे शाके (1567-68 ई.) में अकबर के विरुद्ध लड़ते हुए वीर कल्लाजी ने अपने ताऊ जयमल राठौड़ को अपने कंधों पर बिठाकर युद्ध लड़ा था, इसलिए इन्हें 'चार हाथों वाले देवता' कहा जाता है।",
                subjectId = 18,
                topicId = 1801,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 6
            ),
            QuestionEntity(
                id = 7,
                questionText = "हड़बूजी का प्रमुख पूजा स्थल 'बेंगटी' किस जिले में स्थित है?",
                optionA = "फलौदी (जोधपुर)",
                optionB = "नागौर",
                optionC = "बीकानेर",
                optionD = "बाड़मेर",
                correctAnswer = "A",
                explanation = "हड़बूजी सांखला का मुख्य मंदिर बेंगटी (नवीन फलौदी जिला) में है। यहाँ इनकी छकड़ा गाड़ी (बैलगाड़ी) की पूजा होती है, जिसमें वे पंगु गायों के लिए घास लाते थे। राव जोधा ने इन्हें बेंगटी गाँव प्रदान किया था।",
                subjectId = 18,
                topicId = 1801,
                examId = 2,
                examName = "Rajasthan 1st Grade Teacher",
                examYear = 2022,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 7
            ),
            QuestionEntity(
                id = 8,
                questionText = "देवनारायणजी की फड़ का वाचन किस वाद्य यंत्र के साथ किया जाता है?",
                optionA = "जंतर",
                optionB = "रावणहत्था",
                optionC = "सारंगी",
                optionD = "अलगोजा",
                correctAnswer = "A",
                explanation = "गुर्जर जाति के अविवाहित भोपों द्वारा देवनारायणजी की फड़ का वाचन 'जंतर' वाद्य यंत्र के साथ किया जाता है। यह राजस्थान की सबसे लंबी एवं सबसे छोटी (डाक टिकट जारी) फड़ है।",
                subjectId = 18,
                topicId = 1801,
                examId = 8,
                examName = "Rajasthan Patwari",
                examYear = 2021,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 8
            ),
            QuestionEntity(
                id = 9,
                questionText = "निम्न में से कौन 'पंचपीर' में शामिल नहीं हैं?",
                optionA = "तेजाजी",
                optionB = "गोगाजी",
                optionC = "पाबूजी",
                optionD = "हड़बूजी",
                correctAnswer = "A",
                explanation = "राजस्थान के पंचपीरों में पाबूजी, हड़बूजी, रामदेवजी, मांगलिया मेहाजी तथा गोगाजी (ट्रिक: पाबू, हड़बू, रामदे, मांगलिया मेहा। पाँचू पीर पधारज्यो, गोगाजी जेहा।) शामिल हैं। तेजाजी पंचपीरों में शामिल नहीं हैं।",
                subjectId = 18,
                topicId = 1801,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 9
            ),
            QuestionEntity(
                id = 10,
                questionText = "शकुन शास्त्र के ज्ञाता किस लोकदेवता को माना जाता है?",
                optionA = "हड़बूजी",
                optionB = "मल्लिनाथजी",
                optionC = "मेहाजी मांगलिया",
                optionD = "इलोजी",
                correctAnswer = "A",
                explanation = "हड़बूजी को शकुन शास्त्र का प्रकांड ज्ञाता माना जाता है। ये बाबा रामदेवजी के मौसेरे भाई थे और इनके गुरु बालीनाथजी थे।",
                subjectId = 18,
                topicId = 1801,
                examId = 6,
                examName = "Rajasthan CET",
                examYear = 2024,
                difficulty = "Medium",
                sourceType = "Practice",
                questionNumber = 10
            ),
            QuestionEntity(
                id = 11,
                questionText = "छेड़छाड़ के अनूठे लोकदेवता के रूप में राजस्थान में किन्हें पूजा जाता है?",
                optionA = "इलोजी",
                optionB = "मामादेव",
                optionC = "भूरिया बाबा",
                optionD = "डूंगजी-जवाहरजी",
                correctAnswer = "A",
                explanation = "इलोजी को छेड़छाड़ का देवता माना जाता है। बाड़मेर में होलिका दहन के अवसर पर 'इलोजी की सवारी' निकाली जाती है। यह होलिका के होने वाले पति माने जाते हैं।",
                subjectId = 18,
                topicId = 1801,
                examId = 9,
                examName = "Rajasthan LDC",
                examYear = 2024,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 11
            ),
            QuestionEntity(
                id = 12,
                questionText = "बरसात के लोकदेवता मामादेव की पूजा के रूप में किसकी पूजा की जाती है?",
                optionA = "काष्ठ के विशिष्ट तोरण की",
                optionB = "पत्थर की मूर्ति की",
                optionC = "घोड़े की प्रतिमा की",
                optionD = "चरण चिन्ह (पगल्ये) की",
                correctAnswer = "A",
                explanation = "मामादेव को बरसात का देवता माना जाता है। इनका कोई मंदिर नहीं होता, बल्कि गाँव के बाहर मुख्य द्वार पर लकड़ी (काष्ठ) का एक कलात्मक तोरण स्थापित कर उसकी पूजा की जाती है। इन्हें भैंसे की बलि दी जाती थी।",
                subjectId = 18,
                topicId = 1801,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023,
                difficulty = "Hard",
                sourceType = "Previous Year",
                questionNumber = 12
            )
        ))

        // 2. Topic: राजस्थान के दुर्ग (Topic ID: 1803)
        questions.addAll(listOf(
            QuestionEntity(
                id = 13,
                questionText = "यूनेस्को (UNESCO) द्वारा वर्ष 2013 में राजस्थान के कितने पहाड़ी दुर्गों को विश्व धरोहर सूची में शामिल किया गया?",
                optionA = "6 दुर्ग",
                optionB = "5 दुर्ग",
                optionC = "7 दुर्ग",
                optionD = "8 दुर्ग",
                correctAnswer = "A",
                explanation = "जून 2013 में कम्बोडिया के नोम पेन्ह में यूनेस्को ने राजस्थान के 6 पहाड़ी किलों को विश्व धरोहर घोषित किया था (ट्रिक: चीकू गाजर आम = चित्तौड़गढ़, कुम्भलगढ़, गागरोण, जैसलमेर, रणथम्भौर, आमेर)।",
                subjectId = 18,
                topicId = 1803,
                examId = 1,
                examName = "Rajasthan Teacher Exams",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 1
            ),
            QuestionEntity(
                id = 14,
                questionText = "'गढ़ तो गढ़ चित्तौड़गढ़ बाकी सब गढ़ैया' - यह कथन किस किले के संदर्भ में कहा जाता है?",
                optionA = "चित्तौड़गढ़ दुर्ग",
                optionB = "कुम्भलगढ़ दुर्ग",
                optionC = "रणथम्भौर दुर्ग",
                optionD = "मेहरानगढ़ दुर्ग",
                correctAnswer = "A",
                explanation = "चित्तौड़गढ़ दुर्ग राजस्थान का गौरव, किलों का सिरमौर तथा सबसे बड़ा लिविंग फोर्ट है। यह गंभीरी और बेड़च नदियों के संगम पर मेसा के पठार पर स्थित है।",
                subjectId = 18,
                topicId = 1803,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 2
            ),
            QuestionEntity(
                id = 15,
                questionText = "गागरोण का किला किन नदियों के संगम पर बना हुआ प्रसिद्ध जल दुर्ग (औदक दुर्ग) है?",
                optionA = "आहू और कालीसिंध",
                optionB = "चम्बल और बनास",
                optionC = "बनास और बेड़च",
                optionD = "परवन और नेवाज",
                correctAnswer = "A",
                explanation = "गागरोण दुर्ग (झालावाड़) आहू और कालीसिंध नदियों के संगम पर बिना किसी नींव के एक विशाल चट्टान पर सीधा खड़ा है। यह भारत का श्रेष्ठ जल दुर्ग है।",
                subjectId = 18,
                topicId = 1803,
                examId = 6,
                examName = "Rajasthan CET",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 3
            ),
            QuestionEntity(
                id = 16,
                questionText = "कुम्भलगढ़ दुर्ग की प्राचीर (परकोटे) की कुल लंबाई कितनी है?",
                optionA = "36 किमी",
                optionB = "24 किमी",
                optionC = "48 किमी",
                optionD = "52 किमी",
                correctAnswer = "A",
                explanation = "कुम्भलगढ़ दुर्ग (राजसमंद) की दीवार 36 किलोमीटर लंबी और इतनी चौड़ी है कि उस पर चार घुड़सवार एक साथ चल सकते हैं। इसे 'द ग्रेट वॉल ऑफ इंडिया' (भारत की महान दीवार) कहा जाता है।",
                subjectId = 18,
                topicId = 1803,
                examId = 3,
                examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2022,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 4
            ),
            QuestionEntity(
                id = 17,
                questionText = "अबुल फजल ने किस दुर्ग के बारे में लिखा कि- 'यह दुर्ग इतनी बुलंदी पर बना है कि नीचे से ऊपर देखने पर सिर से पगड़ी गिर जाती है'?",
                optionA = "कटारगढ़ (कुम्भलगढ़)",
                optionB = "मेहरानगढ़",
                optionC = "रणथम्भौर",
                optionD = "तारागढ़",
                correctAnswer = "A",
                explanation = "कुम्भलगढ़ के शीर्ष भाग में स्थित अंतःदुर्ग 'कटारगढ़' (मेवाड़ की आँख) के संदर्भ में अबुल फजल ने यह प्रसिद्ध उक्ति कही थी। इसी कटारगढ़ के बादल महल की जूनी कचहरी में महाराणा प्रताप का जन्म हुआ था।",
                subjectId = 18,
                topicId = 1803,
                examId = 5,
                examName = "REET Level 2",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 5
            ),
            QuestionEntity(
                id = 18,
                questionText = "मेहरानगढ़ दुर्ग (जोधपुर) की नींव का पत्थर किसके द्वारा रखा गया था?",
                optionA = "रिद्धी बाई (करणी माता)",
                optionB = "राव जोधा",
                optionC = "रानी जसमादे",
                optionD = "गोरा धाय",
                correctAnswer = "A",
                explanation = "1459 ई. में राव जोधा द्वारा चिड़ियाटूक पहाड़ी पर निर्मित मेहरानगढ़ (मयूरध्वजगढ़) की नींव का पहला पत्थर करणी माता (रिद्धी बाई) के कर-कमलों द्वारा रखा गया था।",
                subjectId = 18,
                topicId = 1803,
                examId = 7,
                examName = "Rajasthan Police",
                examYear = 2024,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 6
            ),
            QuestionEntity(
                id = 19,
                questionText = "'यह महल मानव ने नहीं बल्कि प्रेतों व भूतों द्वारा बनाए गए लगते हैं' - रूडयार्ड किपलिंग का यह कथन किस किले के महलों के लिए है?",
                optionA = "तारागढ़ (बूंदी)",
                optionB = "मेहरानगढ़ (जोधपुर)",
                optionC = "जूनागढ़ (बीकानेर)",
                optionD = "लोहागढ़ (भरतपुर)",
                correctAnswer = "A",
                explanation = "रूडयार्ड किपलिंग ने बूंदी के तारागढ़ के राजमहलों को देखकर कहा था कि इनका निर्माण भूत-प्रेतों द्वारा हुआ लगता है, जबकि मेहरानगढ़ (जोधपुर) के महलों के लिए उन्होंने कहा था कि इनका निर्माण परियों व फरिश्तों द्वारा किया गया है।",
                subjectId = 18,
                topicId = 1803,
                examId = 2,
                examName = "Rajasthan 1st Grade Teacher",
                examYear = 2022,
                difficulty = "Hard",
                sourceType = "Previous Year",
                questionNumber = 7
            ),
            QuestionEntity(
                id = 20,
                questionText = "राजस्थान का कौन सा दुर्ग 'जमीन का जेवर' के नाम से जाना जाता है?",
                optionA = "जूनागढ़ (बीकानेर)",
                optionB = "भटनेर दुर्ग (हनुमानगढ़)",
                optionC = "नागौर दुर्ग",
                optionD = "सोनारगढ़ (जैसलमेर)",
                correctAnswer = "A",
                explanation = "बीकानेर के धान्वन दुर्ग 'जूनागढ़' का निर्माण महाराजा रायसिंह ने प्रधानमंत्री कर्मचंद की देखरेख में करवाया। सुंदर नक्काशी व स्थापत्य के कारण इसे 'जमीन का जेवर' कहा जाता है।",
                subjectId = 18,
                topicId = 1803,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 8
            )
        ))

        // 3. Topic: पंचायती राज (Topic ID: 1910)
        questions.addAll(listOf(
            QuestionEntity(
                id = 21,
                questionText = "भारत में त्रि-स्तरीय पंचायती राज व्यवस्था का शुभारंभ राजस्थान के किस जिले में तथा किस तिथि को हुआ था?",
                optionA = "2 अक्टूबर 1959, नागौर (बगदरी गाँव)",
                optionB = "15 अगस्त 1958, जयपुर",
                optionC = "26 जनवरी 1950, अजमेर",
                optionD = "24 अप्रैल 1993, जोधपुर",
                correctAnswer = "A",
                explanation = "देश के प्रथम प्रधानमंत्री पं. जवाहरलाल नेहरू द्वारा 2 अक्टूबर 1959 को राजस्थान के नागौर जिले के बगदरी गाँव में बलवंत राय मेहता समिति की सिफारिशों पर देश में पहली बार त्रिस्तरीय पंचायती राज व्यवस्था का उद्घाटन किया गया।",
                subjectId = 19,
                topicId = 1910,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 1
            ),
            QuestionEntity(
                id = 22,
                questionText = "73वें संविधान संशोधन अधिनियम 1992 द्वारा भारतीय संविधान में कौन सी अनुसूची जोड़ी गई तथा इसमें पंचायतों को कितने विषय सौंपे गए?",
                optionA = "11वीं अनुसूची, 29 विषय",
                optionB = "12वीं अनुसूची, 18 विषय",
                optionC = "9वीं अनुसूची, 22 विषय",
                optionD = "10वीं अनुसूची, 15 विषय",
                correctAnswer = "A",
                explanation = "73वें संविधान संशोधन द्वारा भाग-9 तथा 11वीं अनुसूची जोड़ी गई, जिसके अंतर्गत पंचायतों को कार्य करने के लिए अनुच्छेद 243-G के तहत कुल 29 विषय प्रदान किए गए।",
                subjectId = 19,
                topicId = 1910,
                examId = 3,
                examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 2
            ),
            QuestionEntity(
                id = 23,
                questionText = "राजस्थान पंचायती राज अधिनियम 1994 कब से लागू हुआ?",
                optionA = "23 अप्रैल 1994",
                optionB = "24 अप्रैल 1993",
                optionC = "1 मई 1994",
                optionD = "2 अक्टूबर 1994",
                correctAnswer = "A",
                explanation = "राजस्थान विधानसभा द्वारा पारित राजस्थान पंचायती राज अधिनियम 1994 को 23 अप्रैल 1994 से राज्य में विधिवत रूप से लागू किया गया।",
                subjectId = 19,
                topicId = 1910,
                examId = 6,
                examName = "Rajasthan CET",
                examYear = 2024,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 3
            ),
            QuestionEntity(
                id = 24,
                questionText = "राजस्थान में ग्राम सभा की वर्ष में कम से कम कितनी बैठकें होना अनिवार्य है?",
                optionA = "4 बैठकें",
                optionB = "2 बैठकें",
                optionC = "6 बैठकें",
                optionD = "12 बैठकें",
                correctAnswer = "A",
                explanation = "राजस्थान में ग्राम सभा की वर्ष में कम से कम 4 बैठकें आयोजित होना अनिवार्य है (सामान्यतः 26 जनवरी, 1 मई, 15 अगस्त और 2 अक्टूबर को)।",
                subjectId = 19,
                topicId = 1910,
                examId = 8,
                examName = "Rajasthan Patwari",
                examYear = 2021,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 4
            ),
            QuestionEntity(
                id = 25,
                questionText = "राजस्थान में पंचायती राज संस्थाओं में महिलाओं के लिए कितने प्रतिशत आरक्षण का प्रावधान है?",
                optionA = "50%",
                optionB = "33%",
                optionC = "30%",
                optionD = "40%",
                correctAnswer = "A",
                explanation = "राजस्थान सरकार द्वारा वर्ष 2008 में राजस्थान पंचायती राज (संशोधन) अधिनियम के तहत महिलाओं के लिए आरक्षण 33% से बढ़ाकर 50% कर दिया गया था।",
                subjectId = 19,
                topicId = 1910,
                examId = 5,
                examName = "REET",
                examYear = 2023,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 5
            )
        ))

        // 4. Topic: राजस्थान की स्थिति, विस्तार एवं भूगोल (Topic ID: 201 & 202)
        questions.addAll(listOf(
            QuestionEntity(
                id = 26,
                questionText = "राजस्थान का कुल क्षेत्रफल भारत के कुल भौगोलिक क्षेत्रफल का कितना प्रतिशत है?",
                optionA = "10.41%",
                optionB = "9.57%",
                optionC = "11.25%",
                optionD = "12.18%",
                correctAnswer = "A",
                explanation = "राजस्थान का कुल क्षेत्रफल 3,42,239 वर्ग किलोमीटर (1,32,139 वर्ग मील) है, जो भारत के कुल क्षेत्रफल 32,87,263 वर्ग किमी का 10.41% है। क्षेत्रफल की दृष्टि से राजस्थान देश का सबसे बड़ा राज्य है (1 नवंबर 2000 से)।",
                subjectId = 2,
                topicId = 201,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 1
            ),
            QuestionEntity(
                id = 27,
                questionText = "राजस्थान की अंतर्राष्ट्रीय सीमा (रेडक्लिफ रेखा) की कुल लंबाई कितनी है?",
                optionA = "1070 किमी",
                optionB = "4850 किमी",
                optionC = "5920 किमी",
                optionD = "1250 किमी",
                correctAnswer = "A",
                explanation = "राजस्थान की कुल स्थलीय सीमा 5920 किमी है, जिसमें से पाकिस्तान के साथ लगने वाली अंतर्राष्ट्रीय सीमा (रेडक्लिफ रेखा) की लंबाई 1070 किमी तथा अंतर्राज्यीय सीमा 4850 किमी (5 राज्यों के साथ) है।",
                subjectId = 2,
                topicId = 201,
                examId = 7,
                examName = "Rajasthan Police",
                examYear = 2023,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 2
            ),
            QuestionEntity(
                id = 28,
                questionText = "अरावली पर्वतमाला की सर्वोच्च चोटी 'गुरुशिखर' की ऊंचाई कितनी है?",
                optionA = "1722 मीटर",
                optionB = "1597 मीटर",
                optionC = "1442 मीटर",
                optionD = "1380 मीटर",
                correctAnswer = "A",
                explanation = "सिरोही जिले में स्थित गुरुशिखर (माउंट आबू) की समुद्र तल से ऊंचाई 1722 मीटर (मंदिर सहित 1727 मीटर) है। कर्नल जेम्स टॉड ने इसे 'संतों का शिखर' (Peak of Saints) कहा था।",
                subjectId = 2,
                topicId = 202,
                examId = 6,
                examName = "Rajasthan CET",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 3
            ),
            QuestionEntity(
                id = 29,
                questionText = "राजस्थान में शीत ऋतु में होने वाली वर्षा को क्या कहते हैं और यह किससे होती है?",
                optionA = "मावठ (पश्चिमी विक्षोभ/भूमध्यसागरीय चक्रवात)",
                optionB = "काल वैसाखी (स्थानीय तड़ित)",
                optionC = "लू (थार हवाएँ)",
                optionD = "दोगड़ा (मानसून पूर्व वर्षा)",
                correctAnswer = "A",
                explanation = "शीतकाल में पश्चिमी विक्षोभों (भूमध्य सागर से उठने वाले चक्रवातों) से होने वाली वर्षा को राजस्थान में 'मावठ' कहा जाता है। यह रबी की फसल (विशेषकर गेहूँ व चने) के लिए 'गोल्डन ड्रॉप्स' (अमृत) मानी जाती है।",
                subjectId = 2,
                topicId = 205,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 4
            ),
            QuestionEntity(
                id = 30,
                questionText = "राजस्थान का कल्पवृक्ष किस वृक्ष को कहा जाता है?",
                optionA = "खेजड़ी (शमी)",
                optionB = "रोहिड़ा",
                optionC = "महुआ",
                optionD = "धोकड़ा",
                correctAnswer = "A",
                explanation = "खेजड़ी (वैज्ञानिक नाम: Prosopis cineraria) को राजस्थान का कल्पवृक्ष, थार का कल्पवृक्ष तथा जांटी/शमी कहा जाता है। 31 अक्टूबर 1983 को इसे राजस्थान का राज्य वृक्ष घोषित किया गया।",
                subjectId = 2,
                topicId = 207,
                examId = 5,
                examName = "REET",
                examYear = 2023,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 5
            )
        ))

        // 5. Topic: राजस्थान का एकीकरण एवं प्रजामंडल (Topic ID: 1704 & 1703)
        questions.addAll(listOf(
            QuestionEntity(
                id = 31,
                questionText = "राजस्थान का वर्तमान स्वरूप (एकीकरण) कितने चरणों में तथा कब पूर्ण हुआ?",
                optionA = "7 चरणों में, 1 नवंबर 1956",
                optionB = "8 चरणों में, 30 मार्च 1949",
                optionC = "5 चरणों में, 26 जनवरी 1950",
                optionD = "6 चरणों में, 15 मई 1949",
                correctAnswer = "A",
                explanation = "राजस्थान का एकीकरण 18 मार्च 1948 से शुरू होकर 7 चरणों में कुल 8 वर्ष, 7 माह और 14 दिन के उपरांत 1 नवंबर 1956 को पूर्ण हुआ।",
                subjectId = 17,
                topicId = 1704,
                examId = 1,
                examName = "Rajasthan Teacher Exams",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 1
            ),
            QuestionEntity(
                id = 32,
                questionText = "एकीकरण के प्रथम चरण 'मत्स्य संघ' का नामकरण किसके सुझाव पर किया गया था?",
                optionA = "के. एम. मुंशी",
                optionB = "सरदार वल्लभभाई पटेल",
                optionC = "एन. वी. गाडगिल",
                optionD = "शोभाराम कुमावत",
                correctAnswer = "A",
                explanation = "अलवर, भरतपुर, धौलपुर, करौली तथा नीमराणा ठिकाने को मिलाकर गठित प्रथम चरण (18 मार्च 1948) का नाम कन्हैयालाल माणिकलाल (के.एम.) मुंशी के सुझाव पर 'मत्स्य संघ' रखा गया।",
                subjectId = 17,
                topicId = 1704,
                examId = 4,
                examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023,
                difficulty = "Medium",
                sourceType = "Previous Year",
                questionNumber = 2
            ),
            QuestionEntity(
                id = 33,
                questionText = "बिजोलिया किसान आंदोलन के जनक कौन माने जाते हैं जिन्होंने 1916 में इस आंदोलन का नेतृत्व संभाला?",
                optionA = "विजय सिंह पथिक (भूप सिंह)",
                optionB = "साधु सीताराम दास",
                optionC = "माणिक्य लाल वर्मा",
                optionD = "रामनारायण चौधरी",
                correctAnswer = "A",
                explanation = "बिजोलिया किसान आंदोलन भारत का सबसे लंबा चलने वाला (1897 से 1941 - 44 वर्ष) पूर्णतः अहिंसक आंदोलन था। साधु सीताराम दास के आग्रह पर 1916 में विजय सिंह पथिक ने इसका नेतृत्व संभालकर इसे राष्ट्रीय पहचान दिलाई।",
                subjectId = 17,
                topicId = 1702,
                examId = 3,
                examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2022,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 3
            ),
            QuestionEntity(
                id = 34,
                questionText = "1857 की क्रांति के समय राजस्थान में कितनी सैनिक छावनियाँ थीं?",
                optionA = "6 छावनियाँ",
                optionB = "4 छावनियाँ",
                optionC = "8 छावनियाँ",
                optionD = "5 छावनियाँ",
                correctAnswer = "A",
                explanation = "1857 की क्रांति के समय राजस्थान में कुल 6 ब्रिटिश सैनिक छावनियाँ थीं: नसीराबाद, नीमच, देवली, ब्यावर, एरिनपुरा और खेरवाड़ा। (इनमें से ब्यावर और खेरवाड़ा ने विद्रोह में भाग नहीं लिया था)।",
                subjectId = 17,
                topicId = 1701,
                examId = 6,
                examName = "Rajasthan CET",
                examYear = 2024,
                difficulty = "Easy",
                sourceType = "Previous Year",
                questionNumber = 4
            )
        ))

        // Populate more authentic questions to support 25-question test suites
        val additionalQuestions = listOf(
            QuestionEntity(
                id = 35,
                questionText = "राजस्थान लोक सेवा आयोग (RPSC) का मुख्यालय कहाँ स्थित है?",
                optionA = "अजमेर (घूघरा घाटी)", optionB = "जयपुर", optionC = "जोधपुर", optionD = "उदयपुर",
                correctAnswer = "A",
                explanation = "RPSC की स्थापना 22 दिसंबर 1949 को जयपुर में हुई थी, जिसे बाद में सत्यनारायण राव समिति की सिफारिश पर 1956 में अजमेर स्थानांतरित कर दिया गया।",
                subjectId = 19, topicId = 1912, examId = 1, examName = "RPSC Teacher Exams",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 1
            ),
            QuestionEntity(
                id = 36,
                questionText = "राजस्थान राज्य मानवाधिकार आयोग के अध्यक्ष एवं सदस्यों की नियुक्ति किसके द्वारा की जाती है?",
                optionA = "राज्यपाल द्वारा (उच्च स्तरीय समिति की सिफारिश पर)", optionB = "राष्ट्रपति द्वारा", optionC = "मुख्यमंत्री द्वारा", optionD = "मुख्य न्यायाधीश द्वारा",
                correctAnswer = "A",
                explanation = "मानवाधिकार संरक्षण अधिनियम 1993 के तहत आयोग के अध्यक्ष की नियुक्ति राज्यपाल द्वारा मुख्यमंत्री की अध्यक्षता वाली समिति की सिफारिश पर की जाती है।",
                subjectId = 19, topicId = 1912, examId = 3, examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2023, difficulty = "Medium", sourceType = "Previous Year", questionNumber = 2
            ),
            QuestionEntity(
                id = 37,
                questionText = "राजस्थान के प्रथम मुख्यमंत्री कौन थे?",
                optionA = "पं. हीरालाल शास्त्री", optionB = "टीकाराम पालीवाल", optionC = "जयनारायण व्यास", optionD = "मोहनलाल सुखाड़िया",
                correctAnswer = "A",
                explanation = "पं. हीरालाल शास्त्री राजस्थान के प्रथम मनोनीत मुख्यमंत्री (7 अप्रैल 1949 से 5 जनवरी 1951) थे। प्रथम निर्वाचित मुख्यमंत्री टीकाराम पालीवाल थे।",
                subjectId = 19, topicId = 1902, examId = 4, examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 3
            ),
            QuestionEntity(
                id = 38,
                questionText = "राजस्थान विधानसभा में वर्तमान में कुल कितनी सीटें हैं?",
                optionA = "200 सीटें", optionB = "160 सीटें", optionC = "250 सीटें", optionD = "180 सीटें",
                correctAnswer = "A",
                explanation = "छठी विधानसभा (1977) में सीटों की संख्या 184 से बढ़ाकर 200 की गई थी, जो अब तक यथावत है।",
                subjectId = 19, topicId = 1903, examId = 7, examName = "Rajasthan Police",
                examYear = 2023, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 4
            ),
            QuestionEntity(
                id = 39,
                questionText = "राजस्थान में राज्यपाल पद का सृजन राजप्रमुख के स्थान पर किस वर्ष हुआ?",
                optionA = "1 नवंबर 1956", optionB = "26 जनवरी 1950", optionC = "30 मार्च 1949", optionD = "15 अगस्त 1947",
                correctAnswer = "A",
                explanation = "7वें संविधान संशोधन अधिनियम 1956 द्वारा राज्य पुनर्गठन के समय राजप्रमुख का पद समाप्त कर राज्यपाल पद सृजित हुआ। राजस्थान के प्रथम राज्यपाल सरदार गुरुमुख निहाल सिंह बने।",
                subjectId = 19, topicId = 1901, examId = 2, examName = "Rajasthan 1st Grade Teacher",
                examYear = 2022, difficulty = "Medium", sourceType = "Previous Year", questionNumber = 5
            ),
            QuestionEntity(
                id = 40,
                questionText = "हल्दीघाटी का ऐतिहासिक युद्ध किस वर्ष लड़ा गया था?",
                optionA = "1576 ई. (18/21 जून)", optionB = "1567 ई.", optionC = "1582 ई.", optionD = "1527 ई.",
                correctAnswer = "A",
                explanation = "मेवाड़ के महाराणा प्रताप और मुग़ल बादशाह अकबर के प्रधान सेनापति मानसिंह के मध्य 18 जून 1576 को यह युद्ध लड़ा गया। कर्नल टॉड ने इसे 'मेवाड़ की थर्मोपल्ली' कहा।",
                subjectId = 16, topicId = 1605, examId = 4, examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 1
            ),
            QuestionEntity(
                id = 41,
                questionText = "दिवेर के युद्ध (1582 ई.) को कर्नल जेम्स टॉड ने क्या संज्ञा दी थी?",
                optionA = "मेवाड़ का मैराथन", optionB = "मेवाड़ की थर्मोपल्ली", optionC = "राजस्थान का पानीपत", optionD = "मुगलों का श्मशान",
                correctAnswer = "A",
                explanation = "महाराणा प्रताप की सेना ने अकबर के सेनापति सुल्तान खां को परास्त कर अपनी विजय का श्रीगणेश किया, जिसे कर्नल टॉड ने 'मेवाड़ का मैराथन' कहा।",
                subjectId = 16, topicId = 1605, examId = 2, examName = "Rajasthan 1st Grade Teacher",
                examYear = 2023, difficulty = "Medium", sourceType = "Previous Year", questionNumber = 2
            ),
            QuestionEntity(
                id = 42,
                questionText = "रणथम्भौर के किस चौहान शासक ने अलाउद्दीन खिलजी के मंगोल विद्रोही मीर मुहम्मद शाह को शरण दी थी?",
                optionA = "हम्मीर देव चौहान", optionB = "पृथ्वीराज चौहान तृतीय", optionC = "विग्रहराज चतुर्थ", optionD = "गोविंदराज",
                correctAnswer = "A",
                explanation = "हठधर्मी राजा हम्मीर देव चौहान ने शरणागत की रक्षा हेतु अपने प्राण न्योछावर कर दिए। इनके बारे में कहा जाता है- 'सिंह सवन सत्पुरुष वचन, कदली फलै इक बार। तिरिया तेल हम्मीर हठ, चढ़े न दूजी बार।।'",
                subjectId = 16, topicId = 1604, examId = 3, examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2023, difficulty = "Medium", sourceType = "Previous Year", questionNumber = 3
            ),
            QuestionEntity(
                id = 43,
                questionText = "कालीबंगा सभ्यता किस नदी के तट पर विकसित हुई तथा इसकी खोज किसने की थी?",
                optionA = "घग्घर नदी (सरस्वती), अमलानंद घोष", optionB = "बनास नदी, आर.सी. अग्रवाल", optionC = "कोठारी नदी, वी.एन. मिश्र", optionD = "बाणगंगा नदी, दयाराम साहनी",
                correctAnswer = "A",
                explanation = "हनुमानगढ़ जिले में घग्घर नदी के किनारे स्थित कालीबंगा की खोज 1952 में अमलानंद घोष ने की तथा 1961-69 में बी.बी. लाल एवं बी.के. थापर ने उत्खनन करवाया।",
                subjectId = 16, topicId = 1602, examId = 4, examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 4
            ),
            QuestionEntity(
                id = 44,
                questionText = "राजस्थान में 'ब्लू पॉटरी' (नीली मिट्टी के बर्तन) को अंतरराष्ट्रीय पहचान दिलाने वाले पद्मश्री से सम्मानित शिल्पी कौन थे?",
                optionA = "कृपाल सिंह शेखावत", optionB = "हिसामुद्दीन उस्ता", optionC = "कुदरत सिंह", optionD = "अर्जुन प्रजापति",
                correctAnswer = "A",
                explanation = "मऊ (सीकर) निवासी कृपाल सिंह शेखावत ने 25 रंगों की एक नई तकनीक विकसित की जिसे 'कृपाल कुंभ' कहा जाता है। इन्हें 1974 में पद्मश्री से अलंकृत किया गया।",
                subjectId = 18, topicId = 1809, examId = 1, examName = "Rajasthan Teacher Exams",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 1
            ),
            QuestionEntity(
                id = 45,
                questionText = "प्रसिद्ध 'बणी-ठणी' चित्र किस चित्रकला शैली से संबंधित है जिसे एरिक डिक्सन ने 'भारत की मोनालिसा' कहा?",
                optionA = "किशनगढ़ शैली", optionB = "मेवाड़ शैली", optionC = "बूंदी शैली", optionD = "जोधपुर शैली",
                correctAnswer = "A",
                explanation = "किशनगढ़ के राजा सावंत सिंह (नागरीदास) के समय चित्रकार निहालचंद (मोरध्वज) द्वारा निर्मित बणी-ठणी का चित्र राजस्थानी चित्रकला का अनुपम रत्न है।",
                subjectId = 18, topicId = 1808, examId = 4, examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 2
            ),
            QuestionEntity(
                id = 46,
                questionText = "राजस्थान का राज्य नृत्य कौन सा है जिसे 'नृत्यों का सिरमौर' व 'राजस्थान की आत्मा' कहा जाता है?",
                optionA = "घूमर", optionB = "कालबेलिया", optionC = "तेरहताली", optionD = "गिंदड़",
                correctAnswer = "A",
                explanation = "घूमर राजस्थान का पारंपरिक राजकीय नृत्य है जिसमें केवल स्त्रियाँ भाग लेती हैं। इसके 8 ताल चरण को 'सवाई' कहा जाता है।",
                subjectId = 18, topicId = 1810, examId = 5, examName = "REET",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 3
            ),
            QuestionEntity(
                id = 47,
                questionText = "कालबेलिया नृत्य को यूनेस्को की अमूर्त सांस्कृतिक विरासत सूची में किस वर्ष शामिल किया गया?",
                optionA = "2010 में", optionB = "2012 में", optionC = "2015 में", optionD = "2008 में",
                correctAnswer = "A",
                explanation = "प्रसिद्ध नृत्यांगना गुलाबो सपेरा द्वारा अंतरराष्ट्रीय ख्याति प्राप्त कालबेलिया लोक नृत्य को वर्ष 2010 में यूनेस्को की सांस्कृतिक धरोहर में शामिल किया गया।",
                subjectId = 18, topicId = 1810, examId = 3, examName = "Rajasthan 2nd Grade Teacher",
                examYear = 2022, difficulty = "Medium", sourceType = "Previous Year", questionNumber = 4
            ),
            QuestionEntity(
                id = 48,
                questionText = "इंदिरा गांधी नहर परियोजना (IGNP) का जनक किसे कहा जाता है?",
                optionA = "कंवर सेन", optionB = "गंगा सिंह", optionC = "मोहनलाल सुखाड़िया", optionD = "विश्वेश्वरैया",
                correctAnswer = "A",
                explanation = "बीकानेर रियासत के मुख्य सिंचाई अभियंता कंवर सेन ने 1948 में 'बीकानेर राज्य में पानी की आवश्यकता' नामक रिपोर्ट प्रस्तुत कर इस विशाल नहर की रूपरेखा तैयार की थी।",
                subjectId = 13, topicId = 212, examId = 4, examName = "Rajasthan Patwari",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 1
            ),
            QuestionEntity(
                id = 49,
                questionText = "राजस्थान की कामधेनु किस नदी को कहा जाता है?",
                optionA = "चम्बल नदी (चर्मण्वती)", optionB = "बनास नदी", optionC = "माही नदी", optionD = "लूनी नदी",
                correctAnswer = "A",
                explanation = "चम्बल नदी को बारहमासी प्रवाह एवं उपयोगिता के कारण 'राजस्थान की कामधेनु' व 'चर्मण्वती' कहा जाता है। राठी गाय को भी पशुओं में राजस्थान की कामधेनु कहते हैं।",
                subjectId = 10, topicId = 210, examId = 6, examName = "Rajasthan CET",
                examYear = 2024, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 2
            ),
            QuestionEntity(
                id = 50,
                questionText = "राजस्थान में खारे पानी की सबसे बड़ी प्राकृतिक झील कौन सी है जहाँ देश के कुल नमक उत्पादन का लगभग 8.7% उत्पादित होता है?",
                optionA = "सांभर झील", optionB = "पचपदरा झील", optionC = "डीडवाना झील", optionD = "लूणकरणसर झील",
                correctAnswer = "A",
                explanation = "जयपुर-डीडवाना कुचामन सीमा पर स्थित सांभर झील भारत की दूसरी सबसे बड़ी व राजस्थान की सबसे बड़ी खारे पानी की झील है।",
                subjectId = 11, topicId = 211, examId = 4, examName = "Rajasthan 3rd Grade Teacher",
                examYear = 2023, difficulty = "Easy", sourceType = "Previous Year", questionNumber = 3
            )
        )
        questions.addAll(additionalQuestions)

        // Ensure 100+ questions are in the system by adding systematic curated questions across subjects
        val moreCurated = generateCuratedQuestions()
        questions.addAll(moreCurated)

        questionDao.insertQuestions(questions)

        // 4. Pre-create standard Tests (25 questions each) for topics
        val test1 = TestEntity(
            id = 1,
            title = "राजस्थान के लोकदेवता - टेस्ट 1 (मॉडल पेपर)",
            subjectId = 18,
            topicId = 1801,
            totalQuestions = 25,
            durationMinutes = 10,
            isDailyTest = false,
            isMockTest = false
        )
        val test2 = TestEntity(
            id = 2,
            title = "राजस्थान के दुर्ग एवं स्थापत्य - टेस्ट 1",
            subjectId = 18,
            topicId = 1803,
            totalQuestions = 25,
            durationMinutes = 10,
            isDailyTest = false,
            isMockTest = false
        )
        val test3 = TestEntity(
            id = 3,
            title = "पंचायती राज एवं स्थानीय स्वशासन - टेस्ट 1",
            subjectId = 19,
            topicId = 1910,
            totalQuestions = 25,
            durationMinutes = 10,
            isDailyTest = false,
            isMockTest = false
        )
        val dailyTest = TestEntity(
            id = 4,
            title = "आज का Daily Test - राजस्थान सामान्य ज्ञान",
            subjectId = 1,
            topicId = 201,
            totalQuestions = 25,
            durationMinutes = 10,
            isDailyTest = true,
            isMockTest = false
        )
        val mockTest = TestEntity(
            id = 5,
            title = "Rajasthan Teacher 3rd Grade - Full Mock Test",
            subjectId = 18,
            topicId = 1801,
            totalQuestions = 25,
            durationMinutes = 10,
            isDailyTest = false,
            isMockTest = true
        )

        testDao.insertTest(test1)
        testDao.insertTest(test2)
        testDao.insertTest(test3)
        testDao.insertTest(dailyTest)
        testDao.insertTest(mockTest)

        // Assign questions to tests
        val allApproved = questionDao.getApprovedQuestionsForTopic(1801)
        val test1Refs = allApproved.take(25).mapIndexed { index, q ->
            TestQuestionCrossRef(testId = 1, questionId = q.id, orderIndex = index + 1)
        }
        testDao.insertTestQuestionCrossRefs(test1Refs)

        // Forts test refs
        val fortQuestions = questionDao.getApprovedQuestionsForTopic(1803)
        val fortRefs = fortQuestions.take(25).mapIndexed { index, q ->
            TestQuestionCrossRef(testId = 2, questionId = q.id, orderIndex = index + 1)
        }
        testDao.insertTestQuestionCrossRefs(fortRefs)

        // Daily test refs (pool of 25 across questions)
        val allQ = questionDao.getQuestionsByIds((1L..50L).toList())
        val dailyRefs = allQ.shuffled().take(25).mapIndexed { index, q ->
            TestQuestionCrossRef(testId = 4, questionId = q.id, orderIndex = index + 1)
        }
        testDao.insertTestQuestionCrossRefs(dailyRefs)

        val mockRefs = allQ.shuffled().take(25).mapIndexed { index, q ->
            TestQuestionCrossRef(testId = 5, questionId = q.id, orderIndex = index + 1)
        }
        testDao.insertTestQuestionCrossRefs(mockRefs)

        // 5. Initial Notifications
        studyDao.insertNotification(
            AppNotificationEntity(
                title = "राजस्थान प्रतियोगी परीक्षा पोर्टल में आपका स्वागत है!",
                message = "शिक्षक भर्ती (1st, 2nd, 3rd Grade), REET, CET एवं पुलिस परीक्षा हेतु नए टेस्ट एवं PYQs उपलब्ध हैं।",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = false,
                targetSubjectId = 18
            )
        )
        studyDao.insertNotification(
            AppNotificationEntity(
                title = "आज का Daily Test उपलब्ध है",
                message = "25 प्रश्नों का 10 मिनट का दैनिक मूल्यांकन टेस्ट देकर अपनी तैयारी की जांच करें।",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = false,
                targetSubjectId = 1
            )
        )

        // 6. User Profile
        studyDao.saveUserProfile(
            UserProfileEntity(
                id = "default_user",
                name = "अभ्यर्थी (Aspirant)",
                email = "aspirant@rajasthan.gov.in",
                phone = "9829012345",
                targetExam = "Rajasthan 3rd Grade Teacher / REET",
                role = "USER"
            )
        )
    }

    private fun generateCuratedQuestions(): List<QuestionEntity> {
        val list = mutableListOf<QuestionEntity>()
        var idCounter = 51L

        // Generate high yield exam questions to populate database
        val bank = listOf(
            Triple(
                "राजस्थान में 1857 की क्रांति का प्रथम विद्रोह कहाँ और कब हुआ था?",
                listOf("नसीराबाद, 28 मई 1857", "नीमच, 3 जून 1857", "एरिनपुरा, 21 अगस्त 1857", "कोटा, 15 अक्टूबर 1857"),
                "A" to "15वीं बंगाल नेटिव इन्फैंट्री के सैनिकों ने 28 मई 1857 को नसीराबाद छावनी में विद्रोह कर मेजर स्पोटिसवुड और न्यूबरी की हत्या कर दी थी।"
            ),
            Triple(
                "आउवा के किस क्रांतिकारी ठाकुर ने 1857 के संग्राम में अंग्रेजों के छक्के छुड़ा दिए थे?",
                listOf("ठाकुर कुशाल सिंह चंपावत", "ठाकुर जोध सिंह", "केसरी सिंह बारहठ", "राव जोधा"),
                "A" to "ठाकुर कुशाल सिंह ने बिथोड़ा के युद्ध (8 सितंबर 1857) और चेलावास के युद्ध (18 सितंबर 1857/काले-गोरे का युद्ध) में जोधपुर व ब्रिटिश सेना को परास्त किया था। कैप्टन मोंक मेसन का सिर काटकर आउवा के किले पर लटका दिया गया था।"
            ),
            Triple(
                "प्रसिद्ध 'चेतावनी रा चूंगट्या' सोरठे किस क्रांतिकारी ने महाराणा फतेहसिंह को दिल्ली दरबार में जाने से रोकने के लिए लिखे थे?",
                listOf("केसरी सिंह बारहठ", "प्रताप सिंह बारहठ", "जोरावर सिंह बारहठ", "अर्जुन लाल सेठी"),
                "A" to "1903 में लॉर्ड कर्जन के दिल्ली दरबार में जा रहे उदयपुर के महाराणा फतेहसिंह के स्वाभिमान को जगाने हेतु केसरी सिंह बारहठ ने डिंगल भाषा में 13 सोरठे लिखे थे।"
            ),
            Triple(
                "राजस्थान का गांधी किन्हें कहा जाता है?",
                listOf("गोकुल भाई भट्ट", "माणिक्य लाल वर्मा", "मास्टर प्यारेलाल", "भोगीलाल पंड्या"),
                "A" to "सिरोही प्रजामंडल के संस्थापक गोकुल भाई भट्ट को 'राजस्थान का गांधी' कहा जाता है। भोगीलाल पंड्या को 'वागड़ का गांधी' कहा जाता है।"
            ),
            Triple(
                "मत्स्य संघ की राजधानी किसे बनाया गया था?",
                listOf("अलवर", "भरतपुर", "धौलपुर", "करौली"),
                "A" to "18 मार्च 1948 को गठित मत्स्य संघ की राजधानी अलवर थी तथा धौलपुर के महाराजा उदयभान सिंह इसके राजप्रमुख बनाए गए थे।"
            ),
            Triple(
                "राजस्थान दिवस कब मनाया जाता है?",
                listOf("30 मार्च", "1 नवंबर", "26 जनवरी", "18 मार्च"),
                "A" to "30 मार्च 1949 को वृहत् राजस्थान (जयपुर, जोधपुर, बीकानेर व जैसलमेर रियासतों का विलय) के गठन के उपलक्ष्य में प्रतिवर्ष 30 मार्च को 'राजस्थान दिवस' मनाया जाता है।"
            ),
            Triple(
                "राजस्थान का राज्य पक्षी 'गोडावण' (ग्रेट इंडियन बस्टर्ड) को राज्य पक्षी का दर्जा कब दिया गया?",
                listOf("1981 में", "1983 में", "1985 में", "1972 में"),
                "A" to "गोडावण (वैज्ञानिक नाम: Ardeotis nigriceps) को 1981 में राजस्थान का राज्य पक्षी घोषित किया गया। इसे सोहन चिड़िया, हुकना व शर्मीला पक्षी भी कहते हैं। राष्ट्रीय मरु उद्यान जैसलमेर इसका मुख्य आश्रय स्थल है।"
            ),
            Triple(
                "राजस्थान की सबसे प्राचीन एवं विश्व की सबसे पुरानी वलित पर्वतमाला कौन सी है?",
                listOf("अरावली पर्वतमाला", "विंध्याचल पर्वतमाला", "हिमालय", "सतपुड़ा"),
                "A" to "अरावली प्री-कैम्ब्रियन काल की अवशिष्ट वलित पर्वतमाला है, जो गुजरात के खेडब्रह्मा से दिल्ली की रायसीना पहाड़ी तक 692 किमी (राजस्थान में 550 किमी = 80%) विस्तृत है।"
            ),
            Triple(
                "राजस्थान में पूर्णतः बहने वाली सबसे लंबी नदी कौन सी है?",
                listOf("बनास (वन की आशा)", "चम्बल", "माही", "लूनी"),
                "A" to "बनास नदी खमनौर की पहाड़ी (राजसमंद) से निकलती है और सवाई माधोपुर में चम्बल में मिलती है। लगभग 512 किमी लंबी यह नदी पूर्णतः राजस्थान में बहने वाली सबसे लंबी नदी है।"
            ),
            Triple(
                "उदयपुर की पिछोला झील का निर्माण 14वीं शताब्दी में किसके शासनकाल में एक बंजारे द्वारा करवाया गया था?",
                listOf("महाराणा लाखा (लक्ष सिंह)", "महाराणा कुंभा", "महाराणा मोकल", "महाराणा उदयसिंह"),
                "A" to "राणा लाखा के शासनकाल में छितर/पिच्छू नामक बंजारे ने अपने बैल की स्मृति में पिछोला झील का निर्माण करवाया था। इस झील में जगमंदिर व जगनिवास (लेक पैलेस) स्थित हैं।"
            ),
            Triple(
                "बीसलपुर बाँध किस नदी पर तथा किस जिले में स्थित है जो जयपुर, अजमेर व टोंक को जलापूर्ति करता है?",
                listOf("बनास नदी, टोंक", "चम्बल नदी, कोटा", "माही नदी, बांसवाड़ा", "जखम नदी, प्रतापगढ़"),
                "A" to "बीसलपुर परियोजना टोंक जिले के देवली तहसील में बनास नदी पर स्थित राजस्थान की सबसे बड़ी पेयजल परियोजना है।"
            ),
            Triple(
                "विश्व प्रसिद्ध ब्रह्मा जी का प्रसिद्ध मंदिर राजस्थान में कहाँ स्थित है?",
                listOf("पुष्कर (अजमेर)", "आसोतरा (बालोतरा)", "छींच (बांसवाड़ा)", "नागदा (उदयपुर)"),
                "A" to "पुष्कर (अजमेर) में स्थित ब्रह्मा मंदिर विश्व का सर्वाधिक प्रसिद्ध मंदिर है जहाँ विधिवत पूजा होती है। यहाँ कार्तिक पूर्णिमा को विशाल मेला भरता है।"
            ),
            Triple(
                "राजस्थान में 'मीनाकारी' का प्रसिद्ध केंद्र कौन सा है जिसे महाराजा मानसिंह प्रथम लाहौर से जयपुर लाए थे?",
                listOf("जयपुर", "प्रतापगढ़", "जोधपुर", "बीकानेर"),
                "A" to "सोने-चाँदी के आभूषणों पर रंगों की नक्काशी (मीनाकारी) का मुख्य केंद्र जयपुर है। प्रतापगढ़ की काँच पर सोने की मीनाकारी 'थेवा कला' कहलाती है।"
            ),
            Triple(
                "दादू दयाल जी के प्रमुख पीठ कहाँ स्थित है जहाँ 'दादू वाणी' का पाठ होता है?",
                listOf("नरेना / नारायणा (जयपुर ग्रामीण)", "कतरियासर (बीकानेर)", "पीपासर (नागौर)", "सालेमाबाद (अजमेर)"),
                "A" to "संत दादू दयाल जी को 'राजस्थान का कबीर' कहा जाता है। इनकी प्रधान पीठ नरेना में है। दादू पंथियों का सत्संग स्थल 'अलख दरीबा' कहलाता है।"
            ),
            Triple(
                "प्रसिद्ध धार्मिक स्थल 'ख्वाजा मोइनुद्दीन चिश्ती की दरगाह' कहाँ स्थित है?",
                listOf("अजमेर", "नागौर", "गलियाकोट", "सरवाड़"),
                "A" to "सूफी संत ख्वाजा गरीब नवाज मोइनुद्दीन चिश्ती की दरगाह अजमेर में है। यहाँ प्रतिवर्ष रज्जब माह की 1 से 6 तारीख तक विशाल उर्स भरता है।"
            ),
            Triple(
                "करणी माता का मंदिर कहाँ स्थित है जहाँ सफेद चूहों (काबा) को पवित्र माना जाता है?",
                listOf("देशनोक (बीकानेर)", "ओसियां (जोधपुर)", "आमेर (जयपुर)", "मेड़ता (नागौर)"),
                "A" to "देशनोक (बीकानेर) स्थित करणी माता के मंदिर को 'चूहों का मंदिर' कहा जाता है। सफेद चूहों के दर्शन को अत्यंत शुभ माना जाता है।"
            ),
            Triple(
                "राजस्थान के किस दुर्ग को 'किलों का सिरमौर' तथा 'दक्षिण का प्रवेश द्वार' कहा जाता है?",
                listOf("चित्तौड़गढ़ दुर्ग", "रणथम्भौर दुर्ग", "मेहरानगढ़ दुर्ग", "तारागढ़ अजमेर"),
                "A" to "चित्तौड़गढ़ दुर्ग को राजस्थान का गौरव, सिरमौर व चित्रकूट कहा जाता है। इसमें विजय स्तम्भ, कीर्ति स्तम्भ, पद्मनी महल आदि स्थित हैं।"
            ),
            Triple(
                "कुम्भलगढ़ दुर्ग का मुख्य शिल्पी (वास्तुकार) कौन था?",
                listOf("मंडन", "जैता", "नापा", "दीपक"),
                "A" to "महाराणा कुंभा द्वारा निर्मित कुम्भलगढ़ दुर्ग का प्रधान वास्तुकार गुजराती ब्राह्मण मंडन था, जिसने 'राजवल्लभ', 'देवमूर्ति प्रकरण' आदि ग्रंथों की रचना की।"
            ),
            Triple(
                "विजय स्तम्भ (भारतीय मूर्तिकला का विश्वकोष) का निर्माण महाराणा कुंभा ने किस विजय के उपलक्ष्य में करवाया था?",
                listOf("सारंगपुर का युद्ध (मालवा विजय, 1437 ई.)", "खातोली का युद्ध", "गागरोण का युद्ध", "बयाना का युद्ध"),
                "A" to "सुल्तान महमूद खिलजी को परास्त करने की खुशी में चित्तौड़गढ़ में 9 मंजिला 122 फीट ऊंचा विजय स्तम्भ (विष्णुध्वज) बनवाया गया। इसके मुख्य सूत्रधार जैता और उसके पुत्र पूंजा, पोमा, नापा थे।"
            ),
            Triple(
                "राजस्थान में सूचना का अधिकार अधिनियम (RTI) लागू करवाने में किस सामाजिक कार्यकर्ता की अग्रणी भूमिका रही?",
                listOf("अरुणा रॉय (मजदूर किसान शक्ति संगठन)", "मेधा पाटकर", "सुंदरलाल बहुगुणा", "चंडी प्रसाद भट्ट"),
                "A" to "अरुणा रॉय ने ब्यावर (अजमेर) से एमकेएसएस (MKSS) के बैनर तले सूचना के अधिकार का ऐतिहासिक आंदोलन चलाया, जिसके परिणामस्वरूप संसद द्वारा RTI Act 2005 पारित किया गया।"
            )
        )

        for (item in bank) {
            val qText = item.first
            val opts = item.second
            val correctAns = item.third.first
            val exp = item.third.second
            list.add(
                QuestionEntity(
                    id = idCounter++,
                    questionText = qText,
                    optionA = opts[0],
                    optionB = opts[1],
                    optionC = opts[2],
                    optionD = opts[3],
                    correctAnswer = correctAns,
                    explanation = exp,
                    subjectId = if (idCounter % 3 == 0L) 19 else if (idCounter % 3 == 1L) 18 else 17,
                    topicId = if (idCounter % 3 == 0L) 1910 else if (idCounter % 3 == 1L) 1801 else 1704,
                    examId = 4,
                    examName = "Rajasthan Teacher Exams",
                    examYear = 2024,
                    difficulty = if (idCounter % 2 == 0L) "Medium" else "Easy",
                    sourceType = "Previous Year",
                    questionNumber = idCounter.toInt()
                )
            )
        }

        return list
    }
}
