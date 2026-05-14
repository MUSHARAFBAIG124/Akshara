package com.aksharadeepa.tutor.data.local

object LessonSummaries {
    private val summaries = mapOf(
        101L to "Chemical reactions create new substances. Revise how to balance equations, identify oxidation and reduction, and recognise signs such as gas, heat, colour change, or precipitate.",
        102L to "Life processes include nutrition, respiration, transportation, and excretion. Focus on diagrams of the digestive system, lungs, heart, and nephron.",
        103L to "Acids, bases, and salts appear in daily life. Practise pH, indicators, neutralisation, and common examples such as sodium chloride and sodium hydroxide.",
        104L to "Electricity needs clear formula practice. Revise current, potential difference, resistance, Ohm's law, series and parallel circuits, power, and safety fuses.",
        105L to "Control and coordination explains how organisms respond. Revise neurons, reflex action, plant tropisms, and important hormones such as auxin and insulin.",
        201L to "Real numbers covers Euclid division lemma, HCF, LCM, prime factorisation, and decimal expansion of rational numbers.",
        202L to "Polynomials require finding zeroes, understanding degree, and connecting algebraic expressions with their graphs.",
        203L to "Pairs of linear equations can be solved by substitution, elimination, cross multiplication, or graphing. Know when solutions are unique, none, or infinite.",
        204L to "Quadratic equations use factorisation, completing the square, and formula methods. The discriminant tells the nature of roots.",
        205L to "Arithmetic progressions are number patterns with a common difference. Practise nth term and sum of n terms.",
        301L to "European powers came for trade and slowly gained political influence. Track the roles of trading companies, forts, and competition.",
        302L to "British expansion used battles, alliances, and policies such as Subsidiary Alliance and Doctrine of Lapse.",
        303L to "British rule affected administration, education, land revenue, transport, and Indian economic life.",
        304L to "Opposition to British rule came from rulers, soldiers, and local communities. Connect leaders with the regions where they resisted.",
        305L to "Social reform movements questioned harmful practices and promoted education, equality, and new social ideas."
    )

    fun forChapter(chapterId: Long): String {
        return summaries[chapterId]
            ?: "Read this lesson carefully. First understand the main idea, then remember important terms, examples, formulas, dates, and diagrams. After reading, try the quiz and revise the answers you missed."
    }
}
