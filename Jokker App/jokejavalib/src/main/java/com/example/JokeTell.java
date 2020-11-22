package com.example;

import java.util.Random;

 public class JokeTell {

        private String[] jokes;
        private Random random;

        public JokeTell() {
            jokes = new String[5];
            jokes[0] = "Q: Can a kangaroo jump higher than a house? A: Of course, a house doesn’t jump at all.";
            jokes[1] = "Q:Anton, do you think I’m a bad mother? A: My name is Paul.";
            jokes[2] = "My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away";
            jokes[3]="Knock knock...who is there? who?";
            jokes[4]="Two iphones walked into a bar. I forgot the rest";
            random = new Random();
        }


        public String getRandomJoke() {
            return jokes[random.nextInt(jokes.length)];
        }

    }

