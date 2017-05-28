package sample;

import java.util.ArrayList;

/**
 * Created by User on 26.10.2016.
 */
public class StringSearch {

    public static ArrayList<Integer> simpleSearch(String text, String query){
        ArrayList<Integer> result = new ArrayList<>();

        if (text.isEmpty() || query.isEmpty()){
            return result;
        }

        int index = text.indexOf(query);
        if(index<0){
            return result;
        }else{
            while(index>=0){
                result.add(index);
                index = text.indexOf(query, index+1);
            }
        }
        return result;
    }

    public static ArrayList<Integer> simpleSearchIgnoreCase(String text, String query){
        text = text.toLowerCase();
        query = query.toLowerCase();
        return simpleSearch(text, query);
    }

    public static ArrayList<Integer> avtomatSearchIgnoreCase(String text, String query){
        text = text.toLowerCase();
        query = query.toLowerCase();
        return avtomatSearch(text, query);
    }

    public static ArrayList<Integer> avtomatSearch(
            String text, String query){
        if(text.isEmpty() || query.isEmpty()){      //заглушка для 0-х состояний автомата
            return new ArrayList<>();
        }

        int q = 0;
        ArrayList<Character> alpha =
                StringSearch.getAlphabet(query);
        ArrayList<Integer> symbolsPos = new ArrayList<>();
        int[][] matr = createAvtomat(query, alpha);

        for(int i = 0; i< text.length(); i++){
            char c = text.charAt(i);
            int a = -1;
            for(int j = 0; j<alpha.size(); j++){
                if(alpha.get(j)==c){
                    a=j;
                    break;
                }
            }

            if(a==-1){
                q=0;
            }else{
                q = matr[q][a];
            }

            if(q==query.length()){
                symbolsPos.add(i-query.length()+1);
            }
        }
        return symbolsPos;
    }

    public static int[][] createAvtomat(
            String p, ArrayList<Character> alphabet){
        int[][] avtomatMatr =
                new int[p.length()+1][alphabet.size()];
        int m = p.length();
        for(int q = 0; q<=m; q++){
            for(int a = 0; a<alphabet.size(); a++){
                char c = alphabet.get(a);
                int k = Integer.min(m+1, q+2);
                --k;
                while(!(comparePrefix(p, k, q, c))){
                    --k;
                }
                avtomatMatr[q][a] = k;
            }
        }
        return avtomatMatr;
    }

    private static boolean comparePrefix(
            String p, int k, int q, char a){
        if(k==0){
            return true;
        }
        String Pk = p.substring(0, k);
        String PqA;
        if(q!=0) {
            PqA = p.substring(0, q) + a;
        }else{
            PqA = ""+a;
        }
        if(PqA.substring((PqA.length())-(Pk.length()),
                PqA.length()).equals(Pk)){
            return true;
        }else{
            return false;
        }
    }

    public static ArrayList<Character> getAlphabet(
            String query){
        ArrayList<Character> result = new ArrayList<>();

        boolean flag = false;
        for(int i = 0; i<query.length(); i++){
            if(result.size()!=0) {
                for (int j = 0; j < result.size(); j++) {
                    if (query.charAt(i) == result.get(j)) {
                        flag = true;
                        break;
                    }
                }
            }

            if(!flag){
                result.add(query.charAt(i));
            }else{
                flag = false;
            }
        }

        return sortAlphabet(result);
    }

    private static ArrayList<Character> sortAlphabet(
            ArrayList<Character> alpha){
        for(int i = 0; i<alpha.size()-1; i++){
            for(int j = i+1; j<alpha.size(); j++) {
                if (Character.compare(alpha.get(i),
                        alpha.get(j)) > 0) {
                    char tmp = alpha.get(i);
                    alpha.set(i, alpha.get(j));
                    alpha.set(j, tmp);
                }
            }
        }
        return  alpha;
    }
}
