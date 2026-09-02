package com.example.data.local

object BroQuotes {
    val quotes = listOf(
        "Wealth begins with the right mindset.",
        "Your thoughts create your financial reality.",
        "Money is a tool for your purpose.",
        "Small actions lead to big wealth.",
        "Every day, you are becoming more abundant.",
        "Wealth flows to those who serve value.",
        "Your net worth grows with your consciousness.",
        "Financial freedom is a state of mind.",
        "You are worthy of all the wealth you desire.",
        "The more you learn, the more you earn.",
        "Abundance is your natural state.",
        "Focus on growth, not just money.",
        "Your wealth journey starts today.",
        "Wealth is earned by those who act.",
        "Conscious wealth is built daily.",
        "Your financial future is in your hands.",
        "Money follows value. Create value.",
        "Wealth is a practice, not a destination.",
        "Every expense is a choice. Choose wisely.",
        "Your wealth mindset defines your reality."
    )

    fun getRandomQuote(): String = quotes.random()
}
