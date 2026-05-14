package com.aksharadeepa.tutor.data.local

import com.aksharadeepa.tutor.data.local.entity.ChapterEntity
import com.aksharadeepa.tutor.data.local.entity.QuestionEntity
import com.aksharadeepa.tutor.data.local.entity.SubjectEntity

object SampleData {
    val subjects = listOf(
        SubjectEntity(1, "Science"),
        SubjectEntity(2, "Mathematics"),
        SubjectEntity(3, "Social Studies")
    )

    val chapters = listOf(
        ChapterEntity(101, 1, "Chemical Reactions and Equations"),
        ChapterEntity(102, 1, "Life Processes"),
        ChapterEntity(103, 1, "Acids, Bases and Salts"),
        ChapterEntity(104, 1, "Electricity"),
        ChapterEntity(105, 1, "Control and Coordination"),
        ChapterEntity(201, 2, "Real Numbers"),
        ChapterEntity(202, 2, "Polynomials"),
        ChapterEntity(203, 2, "Pair of Linear Equations"),
        ChapterEntity(204, 2, "Quadratic Equations"),
        ChapterEntity(205, 2, "Arithmetic Progressions"),
        ChapterEntity(301, 3, "The Advent of Europeans to India"),
        ChapterEntity(302, 3, "The Extension of the British Rule"),
        ChapterEntity(303, 3, "The Impact of the British Rule"),
        ChapterEntity(304, 3, "Opposition to British Rule"),
        ChapterEntity(305, 3, "Social and Religious Reform Movements")
    )

    val questions = listOf(
        q(101, 1, "A balanced chemical equation follows which law?", "Conservation of mass", "Conservation of speed", "Boyle's law", "Ohm's law", 0),
        q(101, 2, "Rusting is an example of:", "Reduction", "Oxidation", "Neutralisation", "Sublimation", 1),
        q(101, 3, "A reaction that releases heat is called:", "Endothermic", "Exothermic", "Displacement", "Photolysis", 1),
        q(101, 4, "Formation of a precipitate usually means:", "No reaction", "A gas escaped", "An insoluble solid formed", "Only melting happened", 2),
        q(101, 5, "Rancidity mainly affects:", "Metals", "Fats and oils", "Glass", "Water", 1),
        q(102, 1, "Plants prepare food through:", "Respiration", "Photosynthesis", "Excretion", "Circulation", 1),
        q(102, 2, "The human organ that pumps blood is the:", "Lung", "Kidney", "Heart", "Stomach", 2),
        q(102, 3, "The functional unit of kidney is:", "Neuron", "Nephron", "Alveolus", "Villus", 1),
        q(102, 4, "Exchange of gases in lungs occurs in:", "Alveoli", "Arteries", "Villi", "Ureters", 0),
        q(102, 5, "Breaking down food to release energy is:", "Respiration", "Digestion", "Transpiration", "Osmosis", 0),
        q(103, 1, "A solution with pH less than 7 is:", "Basic", "Neutral", "Acidic", "Salty only", 2),
        q(103, 2, "Acid plus base generally forms:", "Salt and water", "Only oxygen", "Only hydrogen", "Sugar", 0),
        q(103, 3, "Litmus turns red in:", "Base", "Acid", "Pure water", "Salt", 1),
        q(103, 4, "Sodium hydroxide is a:", "Strong acid", "Strong base", "Neutral gas", "Metal salt only", 1),
        q(103, 5, "Common salt is chemically:", "NaCl", "HCl", "NaOH", "CaCO3", 0),
        q(104, 1, "SI unit of electric current is:", "Volt", "Ohm", "Ampere", "Watt", 2),
        q(104, 2, "Ohm's law relates voltage, current and:", "Mass", "Resistance", "Temperature only", "Density", 1),
        q(104, 3, "Two resistors in series have resistance that:", "Adds up", "Becomes zero", "Always halves", "Never changes", 0),
        q(104, 4, "Electric power is measured in:", "Joule", "Watt", "Newton", "Pascal", 1),
        q(104, 5, "A fuse protects a circuit from:", "Overcurrent", "Gravity", "Refraction", "Evaporation", 0),
        q(105, 1, "The basic unit of nervous system is:", "Neuron", "Nephron", "Platelet", "Alveolus", 0),
        q(105, 2, "Plant growth towards light is:", "Geotropism", "Phototropism", "Hydrotropism", "Chemotropism", 1),
        q(105, 3, "Insulin is secreted by:", "Liver", "Pancreas", "Kidney", "Lungs", 1),
        q(105, 4, "Reflex actions are controlled mainly by:", "Spinal cord", "Stomach", "Skin", "Teeth", 0),
        q(105, 5, "Auxin is a:", "Plant hormone", "Digestive enzyme", "Blood cell", "Salt", 0),
        q(201, 1, "HCF stands for:", "Highest Common Factor", "High Count Formula", "Half Common Fraction", "Highest Circle Form", 0),
        q(201, 2, "Every composite number can be expressed as product of:", "Only even numbers", "Prime numbers", "Decimals", "Angles", 1),
        q(201, 3, "If two numbers are co-prime, their HCF is:", "0", "1", "2", "10", 1),
        q(201, 4, "LCM is useful for adding fractions with:", "Same numerator", "Different denominators", "No denominators", "No values", 1),
        q(201, 5, "Euclid's division lemma applies to:", "Positive integers", "Only triangles", "Only decimals", "Only angles", 0),
        q(202, 1, "A polynomial of degree 2 is called:", "Linear", "Quadratic", "Cubic", "Constant", 1),
        q(202, 2, "The zero of x - 5 is:", "0", "1", "5", "-5", 2),
        q(202, 3, "A linear polynomial has degree:", "0", "1", "2", "3", 1),
        q(202, 4, "For p(x), a zero makes p(x) equal to:", "0", "1", "x", "10", 0),
        q(202, 5, "The graph of a quadratic polynomial is generally a:", "Line", "Parabola", "Circle", "Point only", 1),
        q(203, 1, "A pair of linear equations can have:", "No solution", "One solution", "Infinitely many solutions", "All of these", 3),
        q(203, 2, "The substitution method means:", "Replacing one variable", "Drawing only", "Multiplying all terms by zero", "Ignoring equations", 0),
        q(203, 3, "Intersecting lines have:", "No solution", "One solution", "Infinite solutions", "Only negative solutions", 1),
        q(203, 4, "Parallel lines have:", "No solution", "One solution", "Infinite solutions", "Same equation", 0),
        q(203, 5, "Coincident lines have:", "Infinite solutions", "No solution", "One solution", "Two solutions", 0),
        q(204, 1, "A quadratic equation has highest power:", "1", "2", "3", "4", 1),
        q(204, 2, "The standard form is:", "ax + b = 0", "ax^2 + bx + c = 0", "a/b = c", "x = y", 1),
        q(204, 3, "The discriminant is:", "b^2 - 4ac", "a + b + c", "2a", "c^2", 0),
        q(204, 4, "If discriminant is zero, roots are:", "Equal", "Imaginary only", "Three roots", "No roots", 0),
        q(204, 5, "Completing the square is a method to find:", "Area", "Roots", "Mean", "Median", 1),
        q(205, 1, "In an arithmetic progression, consecutive terms have a common:", "Product", "Difference", "Ratio", "Square", 1),
        q(205, 2, "The nth term of an AP is:", "a + (n-1)d", "a + nd^2", "n/a", "d/a", 0),
        q(205, 3, "In 2, 5, 8, 11 the common difference is:", "2", "3", "5", "8", 1),
        q(205, 4, "The first term is usually denoted by:", "a", "d", "n", "S", 0),
        q(205, 5, "Sum of first n terms is denoted by:", "Sn", "dn", "an", "rn", 0),
        q(301, 1, "The Europeans first came to India mainly for:", "Trade", "Railways", "Cinema", "Elections", 0),
        q(301, 2, "The Portuguese sailor who reached Calicut was:", "Vasco da Gama", "Robert Clive", "Dalhousie", "Wellesley", 0),
        q(301, 3, "A trading centre is also called a:", "Factory", "Fort only", "Court", "School", 0),
        q(301, 4, "European companies competed for:", "Spices and markets", "Snow", "Deserts", "Coal only", 0),
        q(301, 5, "Fortified settlements helped Europeans protect:", "Trade interests", "Mountains", "Rivers only", "Forests only", 0),
        q(302, 1, "Subsidiary Alliance is associated with:", "Wellesley", "Gandhi", "Ashoka", "Harsha", 0),
        q(302, 2, "Doctrine of Lapse was used by:", "Dalhousie", "Tipu Sultan", "Rani Chennamma", "Vasco da Gama", 0),
        q(302, 3, "The Battle of Plassey was fought in:", "1757", "1857", "1947", "1600", 0),
        q(302, 4, "Robert Clive is linked with the rise of:", "British power in Bengal", "Portuguese rule in Goa", "French rule only", "Dutch rule only", 0),
        q(302, 5, "Expansion of British rule used war and:", "Diplomacy", "Satellites", "Printing only", "Mining only", 0),
        q(303, 1, "Permanent Settlement affected:", "Land revenue", "Ocean tides", "Space travel", "Electricity", 0),
        q(303, 2, "British rule changed Indian:", "Economy and administration", "Planetary motion", "Seasons", "Gravity", 0),
        q(303, 3, "Railways under British rule were built mainly for:", "Administration and trade", "Only tourism", "Only games", "Only temples", 0),
        q(303, 4, "English education expanded during:", "British rule", "Stone Age", "Gupta rule only", "Mauryan rule only", 0),
        q(303, 5, "Commercial crops were grown for:", "Market demand", "No reason", "Only rituals", "Only forests", 0),
        q(304, 1, "Kittur Rani Chennamma resisted:", "British rule", "Portuguese sea travel", "French fashion", "Dutch painting", 0),
        q(304, 2, "Tipu Sultan ruled:", "Mysuru", "Bengal", "Punjab", "Delhi Sultanate", 0),
        q(304, 3, "The 1857 revolt is also called:", "First War of Independence", "Green Revolution", "Quit India", "Salt March", 0),
        q(304, 4, "Opposition to British rule came from soldiers, rulers and:", "People", "Only machines", "Only ships", "Only courts", 0),
        q(304, 5, "Resistance movements showed desire for:", "Freedom", "Higher taxes", "Foreign rule", "More annexation", 0),
        q(305, 1, "Brahmo Samaj was founded by:", "Raja Ram Mohan Roy", "Swami Vivekananda", "Dayananda Saraswati", "Jyotiba Phule", 0),
        q(305, 2, "Arya Samaj was started by:", "Dayananda Saraswati", "Raja Ram Mohan Roy", "Savitribai Phule", "Sir Syed Ahmed Khan", 0),
        q(305, 3, "Ramakrishna Mission is associated with:", "Swami Vivekananda", "Robert Clive", "Lord Dalhousie", "Tipu Sultan", 0),
        q(305, 4, "Social reform movements worked against practices such as:", "Child marriage", "Education", "Public health", "Printing", 0),
        q(305, 5, "Savitribai Phule is remembered for her work in:", "Women's education", "Military rule", "European trade", "Land revenue", 0)
    )

    private fun q(
        chapterId: Long,
        number: Int,
        text: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctIndex: Int
    ) = QuestionEntity(
        id = chapterId * 100 + number,
        chapterId = chapterId,
        questionText = text,
        optionA = optionA,
        optionB = optionB,
        optionC = optionC,
        optionD = optionD,
        correctOptionIndex = correctIndex
    )
}
