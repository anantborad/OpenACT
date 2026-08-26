package com.anantborad.act.main.presentation.util

import com.anantborad.act.main.domain.model.*

val MOCK_USER_STATS = UserStats(
    name = "Alex",
    streak = 12,
    dailyGoal = "3/3 sessions",
    accuracy = 78,
    totalQuestions = 412,
    subjects = listOf(
        SubjectStats("English", 82, "+2%"),
        SubjectStats("Math", 71, "-1%"),
        SubjectStats("Reading", 85, "+4%")
    ),
    weakSkills = listOf(
        SkillStat("Geometry", "Math", 45),
        SkillStat("Vocabulary in Context", "Reading", 52)
    )
)

val MOCK_QUESTIONS = listOf(
    PracticeQuestion(
        id = "q1",
        subject = PracticeSubject.MATH,
        skill = "Algebra",
        text = "If 3x + 5 = 20, what is the value of x?",
        options = listOf(
            QuestionOption("a", "A", "3"),
            QuestionOption("b", "B", "5"),
            QuestionOption("c", "C", "10"),
            QuestionOption("d", "D", "15")
        ),
        correctOptionId = "b",
        explanation = "Subtract 5 from both sides to get 3x = 15. Then divide both sides by 3 to get x = 5."
    ),
    PracticeQuestion(
        id = "q2",
        subject = PracticeSubject.ENGLISH,
        skill = "Grammar & Punctuation",
        text = "Choose the option that best replaces the underlined portion: The team, *who was* favored to win, lost the game.",
        options = listOf(
            QuestionOption("a", "A", "NO CHANGE"),
            QuestionOption("b", "B", "which was"),
            QuestionOption("c", "C", "whom was"),
            QuestionOption("d", "D", "who were")
        ),
        correctOptionId = "b",
        explanation = "A team is a collective noun often treated as a singular entity (\"which was\"). \"Who\" typically refers to individuals, whereas \"which\" refers to a group or thing."
    )
)
