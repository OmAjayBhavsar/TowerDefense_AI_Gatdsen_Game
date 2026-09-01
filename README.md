# GATDSEN Tower Defense Bot

A rule-based Java game bot developed as part of a university programming assignment at Otto-von-Guericke University Magdeburg.

The GATDSEN game environment and framework were provided as part of the course. My contribution was the implementation of the bot logic in `MyBot.java`.

## My Contribution

The bot uses the provided `StaticGameState` and `Controller` interfaces to make automated decisions during each game turn.

My implementation includes:

- Initial tower placement based on the board dimensions
- Selection of new tower positions
- Rule-based selection between different tower types
- Automated enemy deployment
- Periodic tower upgrade attempts
- Integration with the provided GATDSEN game framework

## Technologies & Concepts

- Java
- Object-Oriented Programming
- Inheritance
- Rule-based decision logic
- Existing API/framework integration
- Git

## Bot Structure

The custom bot extends the provided `Bot` class:

```java
public class MyBot extends Bot {
    // Custom decision-making logic
}
