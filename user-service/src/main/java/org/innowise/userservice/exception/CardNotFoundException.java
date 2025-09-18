package org.innowise.userservice.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException() {
        super("Card not found");
    }

    public CardNotFoundException(Long cardId) {
        super("Card with ID " + cardId + " not found");
    }
}
