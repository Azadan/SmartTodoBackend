package com.smarttodo.smarttodo.Exception;

public class TodosNotFoundException extends RuntimeException{
    public TodosNotFoundException(String message) {
        super(message);
    }
}
