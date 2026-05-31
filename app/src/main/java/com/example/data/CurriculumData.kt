package com.example.data

data class BoardQuestion(
    val id: Int,
    val subject: String,
    val year: String,
    val chapter: String,
    val question: String,
    val answer: String,
    val isRepeated: Boolean = false
)

data class QuizQuestion(
    val id: Int,
    val subject: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

object CurriculumData {

    val boardQuestions = listOf(
        // Science Questions
        BoardQuestion(
            id = 1,
            subject = "Science",
            year = "2024",
            chapter = "Chemical Reactions & Equations",
            question = "Why should a magnesium ribbon be cleaned before burning in air? (मैग्नीशियम रिबन को हवा में जलाने से पहले साफ क्यों किया जाता है?)",
            answer = "Magnesium ribbon is cleaned to remove the protective layer of basic magnesium carbonate from its surface, which hinders its reaction with oxygen.",
            isRepeated = true
        ),
        BoardQuestion(
            id = 2,
            subject = "Science",
            year = "2023",
            chapter = "Light - Reflection & Refraction",
            question = "Define the principal focus of a concave mirror. (अवतल दर्पण के मुख्य फोकस को परिभाषित कीजिए।)",
            answer = "Light rays that are parallel to the principal axis of a concave mirror converge at a specific point on the principal axis after reflection. This point is called the principal focus.",
            isRepeated = true
        ),
        BoardQuestion(
            id = 3,
            subject = "Science",
            year = "2022",
            chapter = "Acid, Bases & Salts",
            question = "Why does an aqueous solution of an acid conduct electricity? (अम्ल का जलीय विलयन विद्युत का चालन क्यों करता है?)",
            answer = "An aqueous solution of acid conducts electricity because it dissociates in water to produce free hydrogen/hydronium ions (H+ / H3O+), which act as charge carriers.",
            isRepeated = false
        ),

        // Mathematics Questions
        BoardQuestion(
            id = 101,
            subject = "Mathematics",
            year = "2024",
            chapter = "Quadratic Equations",
            question = "Find the roots of the quadratic equation: 2x² - 7x + 3 = 0. (द्विघात समीकरण 2x² - 7x + 3 = 0 के मूल ज्ञात कीजिए।)",
            answer = "Using the quadratic formula x = [-b ± √(b² - 4ac)] / 2a. Here, a=2, b=-7, c=3.\nDiscriminant (D) = b² - 4ac = (-7)² - 4(2)(3) = 49 - 24 = 25.\nx = [7 ± √25] / 4 => x = (7+5)/4 = 3 AND x = (7-5)/4 = 0.5.\nRoots are 3 and 1/2.",
            isRepeated = true
        ),
        BoardQuestion(
            id = 102,
            subject = "Mathematics",
            year = "2023",
            chapter = "Trigonometry",
            question = "Evaluate: sin 60° cos 30° + sin 30° cos 60°. (मान ज्ञात कीजिए: sin 60° cos 30° + sin 30° cos 60°)",
            answer = "Substitute standard values:\nsin 60° = √3/2, cos 30° = √3/2, sin 30° = 1/2, cos 60° = 1/2.\nSo, (√3/2 * √3/2) + (1/2 * 1/2) = 3/4 + 1/4 = 4/4 = 1.",
            isRepeated = true
        ),
        BoardQuestion(
            id = 103,
            subject = "Mathematics",
            year = "2021",
            chapter = "Triangles",
            question = "State Pythagoras Theorem. (पाइथागोरस प्रमेय का कथन लिखिए।)",
            answer = "In a right-angled triangle, the square of the hypotenuse is equal to the sum of the squares of the other two sides. (AC² = AB² + BC²).",
            isRepeated = true
        ),

        // Social Science Questions
        BoardQuestion(
            id = 201,
            subject = "Social Science",
            year = "2024",
            chapter = "Nationalism in Europe",
            question = "Who was Giuseppe Mazzini? (ज्यूसेपे मेचिनी कौन था?)",
            answer = "Giuseppe Mazzini was an Italian revolutionary who founded secret societies like 'Young Italy' and 'Young Europe' to advocate for a unified, democratic Italian republic.",
            isRepeated = false
        ),
        BoardQuestion(
            id = 202,
            subject = "Social Science",
            year = "2023",
            chapter = "Resources & Development",
            question = "What is soil erosion? Suggest any two measures to prevent it. (मृदा अपरदन क्या है? इसे रोकने के कोई दो उपाय सुझाइए।)",
            answer = "Soil erosion is the washing away or removal of the topsoil layer by natural factors like water and wind. Measures to prevent:\n1. Afforestation (planting more trees)\n2. Terrace farming in hilly regions.",
            isRepeated = true
        ),

        // English Questions
        BoardQuestion(
            id = 301,
            subject = "English",
            year = "2024",
            chapter = "A Letter to God",
            question = "Why did Lencho write a letter to God? (लेंचो ने भगवान को पत्र क्यों लिखा था?)",
            answer = "Lencho wrote a letter to God requesting 100 pesos because his entire standing corn crop was completely destroyed by a massive hailstorm, leaving his family on the verge of starvation.",
            isRepeated = true
        ),
        BoardQuestion(
            id = 302,
            subject = "English",
            year = "2023",
            chapter = "The Thief's Story",
            question = "How did Hari Singh feel when he stole Anil's money? (अनिल के पैसे चुराने पर हरि सिंह को कैसा महसूस हुआ?)",
            answer = "Hari Singh initially felt thrilled and ran away with the notes. However, his conscience quickly made him feel extremely guilty because Anil was highly trusting, kind, and was teaching him how to write and read.",
            isRepeated = false
        ),

        // Hindi Questions
        BoardQuestion(
            id = 401,
            subject = "Hindi",
            year = "2024",
            chapter = "सूरदास के पद",
            question = "गोपियों द्वारा उद्धव को भाग्यवान कहने में क्या व्यंग्य निहित है?",
            answer = "गोपियों द्वारा उद्धव को भाग्यवान कहने में गहरा व्यंग्य निहित है। वास्तव में वे कहना चाहती हैं कि उद्धव अत्यंत अभागे हैं क्योंकि वे साक्षात प्रेम के अवतार श्रीकृष्ण के समीप रहकर भी उनके प्रेम के बंधन में बंधने से वंचित रह गए।",
            isRepeated = true
        ),
        BoardQuestion(
            id = 402,
            subject = "Hindi",
            year = "2023",
            chapter = "बालगोबिन भगत",
            question = "लेखक ने बालगोबिन भगत को 'साधु' क्यों कहा है?",
            answer = "लेखक ने बालगोबिन भगत को साधु इसलिए कहा है क्योंकि उनका जीवन ढोंग और आडंबर से मुक्त था। वे कबीर के आदर्शों पर चलते थे, कभी झूठ नहीं बोलते थे, खरे व्यवहार करते थे और अपनी हर सांस ईश्वर को समर्पित रखते थे, यद्यपि वे एक गृहस्थ थे।",
            isRepeated = true
        )
    )

    val mockQuizzes = mapOf(
        "Mathematics" to listOf(
            QuizQuestion(
                id = 11,
                subject = "Mathematics",
                question = "A quadratic equation ax² + bx + c = 0 has two distinct real roots if:",
                options = listOf("b² - 4ac > 0", "b² - 4ac = 0", "b² - 4ac < 0", "None of these"),
                correctAnswerIndex = 0,
                explanation = "A quadratic equation has distinct real roots if the discriminant (D = b² - 4ac) is positive (> 0)."
            ),
            QuizQuestion(
                id = 12,
                subject = "Mathematics",
                question = "What is the value of (sec² θ - tan² θ)?",
                options = listOf("0", "1", "2", "-1"),
                correctAnswerIndex = 1,
                explanation = "According to basic trigonometric identities, sec² θ - tan² θ = 1 for any angle θ."
            ),
            QuizQuestion(
                id = 13,
                subject = "Mathematics",
                question = "The system of equations x + 2y = 5 and 3x + 6y = 15 has how many solutions?",
                options = listOf("Unique solution", "No solution", "Infinitely many solutions", "Two solutions"),
                correctAnswerIndex = 2,
                explanation = "Here a1/a2 = 1/3, b1/b2 = 2/6 = 1/3, c1/c2 = 5/15 = 1/3. Since all three ratios are equal, the lines are coincident and have infinitely many solutions."
            )
        ),
        "Science" to listOf(
            QuizQuestion(
                id = 21,
                subject = "Science",
                question = "Which acid is present in tomato?",
                options = listOf("Lactic acid", "Citric acid", "Oxalic acid", "Methanoic acid"),
                correctAnswerIndex = 2,
                explanation = "Tomatoes naturally contain Oxalic Acid, giving them a slight tartness."
            ),
            QuizQuestion(
                id = 22,
                subject = "Science",
                question = "At which position should an object be placed in front of a convex lens to get a real, inverted and same sized image?",
                options = listOf("At focus F", "At twice the focal length (2F)", "Between optical center and F", "At infinity"),
                correctAnswerIndex = 1,
                explanation = "Placing an object at 2F of a convex lens produces a real, inverted, in-size image at 2F on the other side."
            ),
            QuizQuestion(
                id = 23,
                subject = "Science",
                question = "Which component of blood is responsible for clotting at the site of injury?",
                options = listOf("Red Blood Cells (RBCs)", "White Blood Cells (WBCs)", "Platelets", "Plasma"),
                correctAnswerIndex = 2,
                explanation = "Platelets release clotting factors that form a mesh of fibrin over cuts, stopping blood loss."
            )
        ),
        "Social Science" to listOf(
            QuizQuestion(
                id = 31,
                subject = "Social Science",
                question = "In India, which organization publishes the currency notes on behalf of the Central Government?",
                options = listOf("State Bank of India", "NITI Aayog", "Reserve Bank of India (RBI)", "Finance Ministry"),
                correctAnswerIndex = 2,
                explanation = "The Reserve Bank of India (RBI) is the supreme central banking institution of India authorized to issue currency."
            ),
            QuizQuestion(
                id = 32,
                subject = "Social Science",
                question = "Which type of soil is most widely spread and highly fertile in North India?",
                options = listOf("Black Soil", "Alluvial Soil", "Red Soil", "Laterite Soil"),
                correctAnswerIndex = 1,
                explanation = "Alluvial soil is depositional soil brought by rivers of the Himalayan system. It covers the Indo-Gangetic plains and is exceptionally fertile."
            )
        ),
        "English" to listOf(
            QuizQuestion(
                id = 41,
                subject = "English",
                question = "Select the correct passive voice of: 'Rahul is writing a poem.'",
                options = listOf(
                    "A poem is wrote by Rahul.",
                    "A poem is being written by Rahul.",
                    "A poem has been written by Rahul.",
                    "A poem was written by Rahul."
                ),
                correctAnswerIndex = 1,
                explanation = "Present continuous changes to 'is/am/are + being + V3' in passive voice. Hence, 'A poem is being written by Rahul' is correct."
            ),
            QuizQuestion(
                id = 42,
                subject = "English",
                question = "Who wrote the play 'The Dear Departed'?",
                options = listOf("William Shakespeare", "Stanley Houghton", "Robert Frost", "Leo Tolstoy"),
                correctAnswerIndex = 1,
                explanation = "'The Dear Departed' is a satirical one-act play on social relationships written by Stanley Houghton."
            )
        ),
        "Hindi" to listOf(
            QuizQuestion(
                id = 51,
                subject = "Hindi",
                question = "'रामचरितमानस' किस कवि की रचना है?",
                options = listOf("सूरदास", "कबीरदास", "गोस्वामी तुलसीदास", "रसखान"),
                correctAnswerIndex = 2,
                explanation = "रामचरितमानस अवधी भाषा में महाकवि गोस्वामी तुलसीदास द्वारा रचित अत्यंत लोकप्रिय ग्रंथ है।"
            ),
            QuizQuestion(
                id = 52,
                subject = "Hindi",
                question = "'दो टूक कलेजे के करता, पछताता पथ पर आता' - इन पंक्तियों में कौन सा रस है?",
                options = listOf("सूरदास", "कबीरदास", "गोस्वामी तुलसीदास", "रसखान"), // Standard options placeholder
                correctAnswerIndex = 1,
                explanation = "इन पंक्तियों में भिक्षुक की लाचारी और गरीबी का वर्णन है, जिससे पाठक के मन में करुणा/दया जाग्रत होती है। अतः यहाँ करुण रस है।"
            )
        )
    )
}
