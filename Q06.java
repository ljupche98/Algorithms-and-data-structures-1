import java.io.*;
import java.util.*;

/**
a AND NOT b OR a AND TRUE OR b AND NOT c
a AND NOT (b OR a) AND (TRUE OR b) AND NOT c
a AND NOT (b OR d) AND (TRUE OR e) AND NOT c
a AND NOT ((b OR a) AND NOT d OR NOT c) AND (TRUE OR b) AND NOT c
a AND NOT (      (     b OR a) AND NOT d OR NOT c) AND (TRUE OR b) AND NOT c

a AND NOT b OR a AND TRUE OR b AND NOT c
a AND NOT (b OR a) AND (TRUE OR b) AND NOT c
(((a AND NOT ((b OR a) AND NOT d OR NOT c) AND (TRUE OR b) AND NOT c)))
a AND NOT (      (     b OR a) AND NOT d OR NOT c) AND (TRUE OR b) AND NOT c
**/

/**
Hard coded operations (and operands):
0 -> FALSE
1 -> TRUE
2 -> OR
3 -> AND
4 -> NOT
5+ ... variables
**/

class Pair
{
	public int first;
	public int second;
	
	public Pair(int a, int b)
	{
		first = a;
		second = b;
	}
}

class BinaryTreeNode
{
	public int operation;
	public BinaryTreeNode left;
	public BinaryTreeNode right;
	public BinaryTreeNode parent;
	public ArrayList<Pair> cache = new ArrayList<Pair>();
	
	public BinaryTreeNode(int op)
	{
		operation = op;
		left = right = parent = null;
	}
	
	public BinaryTreeNode(int op, BinaryTreeNode p, BinaryTreeNode l, BinaryTreeNode r)
	{
		operation = op;
		parent = p;
		left = l;
		right = r;
	}
}

class BinaryTree
{
	public final String[] operations = {"FALSE", "TRUE", "OR", "AND", "NOT"};
	public ArrayList<String> operands;
	
	public BinaryTreeNode root;
	
	public BinaryTree(BinaryTreeNode r, ArrayList<String> op)
	{
		root = r;
		operands = op;
	}
	
	public int print(BinaryTreeNode v, int remaining)
	{
		if (v == null) return remaining;
		
		System.out.print((v.operation > 4 ? operands.get(v.operation - 5) : operations[v.operation]) + (--remaining > 0 ? "," : "\n"));
		
		return print(v.right, print(v.left, remaining));
	}
	
	public int print(BinaryTreeNode v, int remaining, PrintWriter writer)
	{
		if (v == null) return remaining;
		
		writer.print((v.operation > 4 ? operands.get(v.operation - 5) : operations[v.operation]) + (--remaining > 0 ? "," : "\n"));
		
		return print(v.right, print(v.left, remaining, writer), writer);
	}
	
	public int countNodes(BinaryTreeNode v)
	{
		if (v == null) return 0;
		
		return 1 + countNodes(v.left) + countNodes(v.right);
	}
	
	public int height(BinaryTreeNode v)
	{
		if (v == null) return 0;
		
		return 1 + Math.max(height(v.left), height(v.right));
	}
	
	public BinaryTreeNode transform(BinaryTreeNode v)
	{
		if (v == null) return null;
		
		/** NOT **/
		if (v.operation == 4)
		{
			/// OPERAND
			if (v.left.operation > 4) return v;
			
			/// TRUE | FALSE
			if (v.left.operation < 2)
			{
				v.left.operation ^= 1;
			/// v.left.operation = 1 - v.left.operation;
				return v.left;
			}
			
			/// OR | AND
			if (v.left.operation < 4)
			{
				BinaryTreeNode newNode = new BinaryTreeNode(v.left.operation ^ 1, v.parent, new BinaryTreeNode(4, null, v.left.left, null), new BinaryTreeNode(4, null, v.left.right, null));
				
				newNode.left.left.parent = newNode.left;
				newNode.right.left.parent = newNode.right;
				
				newNode.left = transform(newNode.left);
				newNode.right = transform(newNode.right);
				
				if (newNode.parent == null) return newNode;
				
				if (newNode.parent.left == v) newNode.parent.left = newNode;
				else newNode.parent.right = newNode;
				
				return newNode;
			}
			
			/// MULTIPLE NOTs
			int count = 0;
			BinaryTreeNode it = v;
			
			while (it.operation == 4)
			{
				count++;
				it = it.left;
			}
			
			if (count % 2 == 0)
				return transform(it);
			
			v.left = it;
			return transform(v);
		}
		
		v.left = transform(v.left);
		v.right = transform(v.right);
		return v;
	}
	
	public void transform()
	{
		root = transform(root);
	}
	
	/** BUILDS CACHE ON EACH NODE OF THE TREE ... OPERAND VALUES FOR WHICH THE EXPRESSION WITH ROOT AT V IS TRUE **/
	
	public ArrayList<Pair> findAllCombinations(BinaryTreeNode v, boolean target, int fixed, int mask, int rev)
	{
		ArrayList<Pair> res = new ArrayList<Pair>();
		
		if (v == null) return res;
		
		if ((v.operation ^ rev) < 2)
		{
			if (v.operation == (target ? 1 : 0)) return v.cache = res;
			return v.cache = null;
		}
		
		if ((v.operation ^ rev) == 2)
		{
			ArrayList<Pair> lc = findAllCombinations(v.left,  target, fixed, mask, rev);
			ArrayList<Pair> rc = findAllCombinations(v.right, target, fixed, mask, rev);
			
			if (lc == null && rc == null) return v.cache = null;
			
			if (lc == null) return v.cache = rc;
			
			if (rc == null) return v.cache = lc;
			
			if (lc.size() == 0 || rc.size() == 0) return v.cache = res;
			
			for (int i = 0; i < lc.size(); i++) res.add(lc.get(i));
			for (int i = 0; i < rc.size(); i++) res.add(rc.get(i));
			
			return v.cache = res;
		}
		
		if ((v.operation ^ rev) == 3)
		{
			ArrayList<Pair> lc = findAllCombinations(v.left,  target, fixed, mask, rev);
			ArrayList<Pair> rc = findAllCombinations(v.right, target, fixed, mask, rev);
			
			if (lc == null || rc == null) return v.cache = null;
			
			if (lc.size() == 0 && rc.size() == 0) return res;
			
			if (rc.size() == 0) return v.cache = lc;
			
			if (lc.size() == 0) return v.cache = rc;
			
			for (int i = 0; i < lc.size(); i++)
			{
				for (int j = 0; j < rc.size(); j++)
				{
					if ((((lc.get(i).first & rc.get(j).first) & lc.get(i).second) ^ ((lc.get(i).first & rc.get(j).first) & rc.get(j).second)) != 0) continue;
					
					/**
					if ((((lc.get(i).first & rc.get(j).first) & lc.get(i).second) & rc.get(j).second) == rc.get(j).second)
						res.add(new Pair(lc.get(i).first, lc.get(i).second));
					else if ((((lc.get(i).first & rc.get(j).first) & lc.get(i).second) & rc.get(j).second) == lc.get(i).second)
						res.add(new Pair(rc.get(j).first, rc.get(j).second));
					else res.add(new Pair(lc.get(i).first | rc.get(j).first, lc.get(i).second | rc.get(j).second));
					**/
					
					res.add(new Pair(lc.get(i).first | rc.get(j).first, lc.get(i).second | rc.get(j).second));
				}
			}
			
			return v.cache = res;
		}
		
		if (v.operation == 4) return findAllCombinations(v.left, !target, fixed, mask, rev ^ 1);
		
		if ((fixed & (1 << (v.operation - 5))) == 0)
		{
			res.add(new Pair(fixed | (1 << (v.operation - 5)), mask | ((target ? 1 : 0) << (v.operation - 5))));
			return v.cache = res;
		}
		
		if ((mask & (1 << (v.operation - 5))) == ((target ? 1 : 0) << (v.operation - 5))) return v.cache = res;
		
		return v.cache = null;
	}
	
	/** NAIVE SOLUTION ... v1 **/
	
	public int calcExpValue(BinaryTreeNode v, int mask, int rev)
	{
		if (v == null) return 1;
		
		if ((v.operation ^ rev) < 2) return v.operation ^ rev;
		
		if ((v.operation ^ rev) == 2) return calcExpValue(v.left, mask, rev) | calcExpValue(v.right, mask, rev);
		
		if ((v.operation ^ rev) == 3) return calcExpValue(v.left, mask, rev) & calcExpValue(v.right, mask, rev);
		
		if (v.operation == 4) return calcExpValue(v.left, ~mask, rev ^ 1);
		
		return ((mask & (1 << (v.operation - 5))) >> (v.operation - 5));
	}
	
	public int findAllCombinations(BinaryTreeNode v)
	{
		int res = 0;
		
		for (int i = 0; i < (1 << operands.size()); i++)
			res += calcExpValue(v, i, 0);
		
		return res;
	}
	
	/** SIMILAR TO NAIVE BUT USES CACHE ... v2 **/
	
	public int findAllCombinationsv2(BinaryTreeNode v)
	{
		if (v.cache == null) return 0;
		
		int res = 0;
		
		for (int i = 0; i < (1 << operands.size()); i++)
		{
			for (int j = 0; j < v.cache.size(); j++)
			{
				if (((v.cache.get(j).first & i) ^ (v.cache.get(j).first & v.cache.get(j).second)) != 0) continue;
				res++; break;
			}
		}
		
		return res;
	}
	
	public int countBits(int mask, int N)
	{
		int res = 0;
		
		for (int i = 0; i < N; i++)
			if ((mask & (1 << i)) != 0)
				res++;
		
		return res;
	}
	
	/** BASED ON SET UNION / INSERSECTION, WORKS FASTER THAN V2 WHEN root.cache.size() < operands.size() ... v3 **/
	
	public int findAllCombinationsv3(BinaryTreeNode v)
	{
		if (v.cache == null) return 0;
		
		if (v.cache.size() == 0) return (1 << operands.size());
		
		ArrayList<Pair> extreme = new ArrayList<Pair>();
		ArrayList<Integer> idx = new ArrayList<Integer>();
		
		for (int i = 0; i < v.cache.size(); i++) {idx.add(i + 1); extreme.add(new Pair(v.cache.get(i).first, v.cache.get(i).second));}
		
		int res = 0, mult = 1;
		
		while (extreme.size() > 0)
		{
			ArrayList<Pair> newex = new ArrayList<Pair>();
			ArrayList<Integer> aux = new ArrayList<Integer>();
			
			for (int i = 0; i < extreme.size(); i++)
			{
				res += mult * (1 << (operands.size() - countBits(extreme.get(i).first, operands.size())));
				
				if (extreme.get(i).first == ((1 << operands.size()) - 1)) continue;
				
				for (int j = idx.get(i); j < v.cache.size(); j++)
				{
					if ((extreme.get(i).first & v.cache.get(j).first) == 0 || (((extreme.get(i).first & v.cache.get(j).first) & extreme.get(i).second) ^ ((extreme.get(i).first & v.cache.get(j).first) & v.cache.get(j).second)) == 0)
					{
						aux.add(j + 1);
						newex.add(new Pair(extreme.get(i).first | v.cache.get(j).first, extreme.get(i).second | v.cache.get(j).second));
					}
				}
			}

			mult *= -1;
			idx = aux;
			extreme = newex;
		}
		
		return res;
	}
	
	public void convert(Pair a)
	{
		for (int i = 0; i < operands.size(); i++)
		{
			if ((a.first & (1 << i)) != 0)
				System.out.print((a.second & (1 << i)) >> i);
			else
				System.out.print("*");
		}
	}
	
	public void sprint(BinaryTreeNode v)
	{
	/// System.out.println((v.operation > 4 ? operands.get(v.operation - 5) : operations[v.operation]));
		
		if (v.cache == null) return;
		
		if (v.cache.size() == 0) convert(new Pair(0, 0));
		
		for (int i = 0; i < v.cache.size(); i++)
		{
			convert(v.cache.get(i)); System.out.print(" ");
		}
	}
	
	/** REMOVES USELESS PARANTHESES FROM THE EXPRESSION **/
	
	public String printExpression(BinaryTreeNode v)
	{
		if (v == null) return new String("");
		
		if (v.operation == 4) return new String("NOT " + (2 <= v.left.operation && v.left.operation <= 3 ? "(" : "") + printExpression(v.left) +
														 (2 <= v.left.operation && v.left.operation <= 3 ? ")" : ""));
				 
		if (v.operation == 2) return new String(printExpression(v.left) + " OR " + printExpression(v.right));
		
		if (v.operation == 3)
			return new String((v.left.operation  == 2 ? "(" : "") + printExpression(v.left)  + (v.left.operation  == 2 ? ")" : "") + " AND " +
							  (v.right.operation == 2 ? "(" : "") + printExpression(v.right) + (v.right.operation == 2 ? ")" : ""));
	  
		return new String((v.operation > 4 ? operands.get(v.operation - 5) : operations[v.operation]));
	}
	
	public void print(int QNO, boolean STDIO, String[] args)
	{
		if (STDIO) {print(root, countNodes(root)); if (QNO == 6) System.out.println(height(root));}
		else try 
		{
			PrintWriter writer = new PrintWriter(args[1]);
			print(root, countNodes(root), writer); if (QNO == 6) writer.println(height(root));
			writer.flush(); writer.close();
		}
		catch (FileNotFoundException e) {}
	/// System.out.println(printExpression(root));
	}
	
	public void findAllCombinations(boolean STDIO, String[] args)
	{
		root.cache = findAllCombinations(root, true, 0, 0, 0);
		
		if (STDIO)
		{
			if (root.cache == null) 						System.out.println(0);
			else if (root.cache.size() == 0) 				System.out.println(1 << operands.size());
			else if (root.cache.size() < operands.size()) 	System.out.println(findAllCombinationsv3(root));
			else 											System.out.println(findAllCombinationsv2(root));
		}
		else try
		{
			PrintWriter writer = new PrintWriter(args[1]);
			if (root.cache == null) 						writer.println(0);
			else if (root.cache.size() == 0) 				writer.println(1 << operands.size());
			else if (root.cache.size() < operands.size()) 	writer.println(findAllCombinationsv3(root));
			else 											writer.println(findAllCombinationsv2(root));
			writer.flush(); writer.close();
		}
		catch (FileNotFoundException e) {}
	}
}

public class Q06
{
	public static final int QNO = 6;
	public static final boolean STDIO = true;
	
	public static final String[] operations = {"FALSE", "TRUE", "OR", "AND", "NOT"};
	
	public static String expression;
	public static BinaryTree root;
	public static ArrayList<String> operands = new ArrayList<String>();
	
	public static void readData(Scanner cin)
	{
		expression = removeExcessiveSpaces(cin.nextLine());
	}
	
	public static void input(String[] args)
	{
		if (STDIO) readData(new Scanner(System.in));
		else try
		{
			readData(new Scanner(new File(args[0])));
		}
		catch (FileNotFoundException e) {}
	}
	
	public static String removeExcessiveSpaces(String word)
	{
		/** TODO **/
		return word;
	}
	
	public static String removeLeadingSpaces(String word)
	{
		int startIdx = 0;
		
		while (startIdx < word.length() && word.charAt(startIdx) == ' ') startIdx++;
		
		return word.substring(startIdx);
	}
	
	public static String readNextExpression(String word, int startIdx)
	{
		while (startIdx < word.length() && word.charAt(startIdx) == ' ') startIdx++;
		
		if (word.charAt(startIdx) == '(') return insideParentheses(word, startIdx);
		
		return readNextWord(word, startIdx);
	}
	
	public static String readNextWord(String word, int startIdx)
	{
		while (startIdx < word.length() && word.charAt(startIdx) == ' ') startIdx++;
		
		int endIdx = startIdx;
		
		while (endIdx < word.length() && word.charAt(endIdx) != ' ') endIdx++;
		
		return word.substring(startIdx, endIdx);
	}
	
	public static String insideParentheses(String word, int startIdx)
	{
		/// 	"((b OR a) AND NOT d OR NOT c)" -> "(b OR a) AND NOT d OR NOT c"
		///      ^
		
		int parCount = 0;
		int endIdx = startIdx;
		
		do
		{
			parCount += (word.charAt(endIdx) == '(' ? 1 : 0);
			parCount -= (word.charAt(endIdx) == ')' ? 1 : 0);
			endIdx++;
		}
		while (endIdx < word.length() && parCount != 0);
		
		return word.substring(startIdx + 1, endIdx - 1);		
	}
	
	public static void debug(Scanner cin)
	{
		System.out.println(expression);
		
		for (int i = 0; i < 5; i++)
			System.out.println(removeLeadingSpaces(readNextExpression(expression, cin.nextInt())));
		///	System.out.println(insideParentheses(expression, cin.nextInt()));
		/// System.out.println(readNextWord(expression, cin.nextInt()));
	}
	
	public static int getOperationCode(String word, int start, int finish)
	{
		for (int i = start; i <= finish; i++)
			if (word.equals(operations[i]))
				return i;
		return -1;
	}
	
	public static BinaryTreeNode createBinaryTree(String word)
	{
		/** OPERAND **/
		
		if(readNextWord(word, 0).equals(word))
		{
			int operationCode = getOperationCode(word, 0, 1);
			
		///	System.out.printf("OPERAND |%s| CREATED\n", word);
			
			if (operationCode == -1)
			{
				for (int i = 0; i < operands.size() && operationCode == -1; i++)
					if (operands.get(i).equals(word))
						operationCode = i + 5;
				
				if (operationCode == -1)
				{
					operands.add(word);
					operationCode = operands.size() + 4;
				}
			}
			
			return new BinaryTreeNode(operationCode);
		}
		
		/** (EXPRESSION) **/
		
		if (readNextExpression(word, 0).equals(word.substring(1, word.length() - 1)))
			return createBinaryTree(readNextExpression(word, 0));
		
		int minOp = 5, minOpIdx = -1;
		
		int startIdx = 0;
		
		while (startIdx < word.length())
		{
			/** (nextExp) OR nextExp **/
			
			String nextExp = readNextExpression(word, startIdx);
			int operationCode = getOperationCode(nextExp, 2, 4);
			
			/** + (operationCode == 4) FOR CONSECUTIVE NOTs **/
			if (operationCode != -1 && operationCode > 1 && operationCode + (operationCode == 4 ? 1 : 0) <= minOp)
			{
				minOp = operationCode;
				minOpIdx = startIdx;
			}
			
			startIdx += nextExp.length() + 1 + 2 * (word.charAt(startIdx) == '(' ? 1 : 0);
		}
		
	///	System.out.printf("ROOTING AT OP |%s| (%d)\n", operations[minOp], minOpIdx);
		
		BinaryTreeNode troot = new BinaryTreeNode(minOp);
		
		/** ONLY NOT FOUND IN word **/
		if (minOp == 4)
		{
	///		System.out.printf("FROM |%s| TO |%s| (ROOTED AT NOT)\n", word, word.substring(minOpIdx + operations[minOp].length() + 1));
			troot.left = createBinaryTree(word.substring(minOpIdx + operations[minOp].length() + 1));
			troot.left.parent = troot;
			troot.right = null;
			return troot;
		}
		
		/** AND or OR FOUND IN word **/
	///	System.out.printf("FROM |%s| TO >LFT< |%s| I >RGT< |%s|\n", word, word.substring(0, minOpIdx - 1), word.substring(minOpIdx + operations[minOp].length() + 1));
		troot.left = createBinaryTree(word.substring(0, minOpIdx - 1));
		troot.left.parent = troot;
		troot.right = createBinaryTree(word.substring(minOpIdx + operations[minOp].length() + 1));
		troot.right.parent = troot;
		return troot;
	}
	
	public static void main(String[] args)
	{
		input(args);
		root = new BinaryTree(createBinaryTree(expression), operands);
		if (QNO == 7) {root.findAllCombinations(STDIO, args); return;}
		if (QNO == 8) root.transform();
		root.print(QNO, STDIO, args);
	}
}