class Solution {
    public boolean isPalindrome(int x) {
        String str = Integer.toString(x);
        for(int i = 0; i < str.length; i++){
            if(str.charAt(i) = str.charAt(str.length() - i)){
                return true;
            }
        }
    }
}

