import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Stack;

class Bracket {
    Bracket(char type, int position) {
        this.type = type;
        this.position = position;
    }

    boolean Match(char c) {
        if (this.type == '[' && c == ']')
            return true;
        if (this.type == '{' && c == '}')
            return true;
        if (this.type == '(' && c == ')')
            return true;
        return false;
    }

    char type;
    int position;
}

class check_brackets {
    public static void main(String[] args) throws IOException {
        InputStreamReader input_stream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input_stream);
        String text = reader.readLine();
        int count=0;
        int flag=0;
        Stack<Bracket> opening_brackets_stack = new Stack<Bracket>();
        for (int position = 0; position < text.length(); ++position) {
            char next = text.charAt(position);
            if (next == '(' || next == '[' || next == '{') {
            	Bracket open_bracket= new Bracket(next,position); 
            	opening_brackets_stack.push(open_bracket);
            }

            if (next == ')' || next == ']' || next == '}') {
            	count=position;
            	if (opening_brackets_stack.empty()){
            		System.out.println(position+1);
            		flag=1;
            		break;}          
            	else if (opening_brackets_stack.peek().Match(next)){
            			opening_brackets_stack.pop();}
            	else {
            		System.out.println(position+1);
            		flag=1;
            		break;}            	
            	}
        }
        if ((count<=text.length()-1) && opening_brackets_stack.empty() && flag==0)
        	System.out.println("Success!");
        else if (opening_brackets_stack.empty()==false && flag==0)
        		System.out.println(count+1);
    }
}
