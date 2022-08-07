package mybnb;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.*;

public class CommentMethods {
    public AccountMethod accountMethod;

    public CommentMethods() {
        this.accountMethod = new AccountMethod();
    }

    public Document generateDocument(int lid)  {
        ResultSet comments = accountMethod.getCommentsAboutListing(lid);
        StringBuilder allComments = new StringBuilder();

        try {
            while (comments.next()) {
                String comment = comments.getString("comment");
                allComments.append(comment).append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Failed to analyse comments of listing " + lid);
            return null;
        }

        if (allComments.isEmpty()) {
            System.out.println("Listing " + lid + " has no comments.");
            return null;
        }

        return new Document(allComments.toString());
    }

    public Map<String, Integer> analyseDocument(Document doc) {
        Map<String, Integer> np_freq = new HashMap<>();
        List<Sentence> sentences = doc.sentences();
        for (Sentence sen: sentences) {
            // Get all noun phrases
            ArrayList<String> nps = extractNounPhrases(sen.parse());
            for (String np : nps) {
                if (np_freq.containsKey(np)) {
                    np_freq.replace(np, np_freq.get(np) + 1);
                } else {
                    np_freq.put(np, 1);
                }
            }
        }
        return sortByFrequency(np_freq);
    }

    public ArrayList<String> extractNounPhrases(Tree tree) {
        ArrayList<String> nps = new ArrayList<>();
        TregexPattern patternNP = TregexPattern.compile("@NP");
        TregexMatcher matcher = patternNP.matcher(tree);
        while (matcher.findNextMatchingNode()) {
            Tree npTree = matcher.getMatch();
            List<Tree> leaves = npTree.getLeaves();
            ArrayList<Word> phrase = new ArrayList<>();
            for (Tree leaf : leaves) {
                ArrayList<Word> words = leaf.yieldWords();
                phrase.addAll(words);
            }
            nps.add(phrase.stream().map(Object::toString).collect(Collectors.joining(" ")).toLowerCase());
        }
        return nps;
    }

    public Map<String, Integer> sortByFrequency(Map<String, Integer> unsort) {
        List<Entry<String, Integer>> list = new LinkedList<>(unsort.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }
}
