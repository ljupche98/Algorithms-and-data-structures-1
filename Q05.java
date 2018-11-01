import java.util.*;

public class Q05
{
	public static int lim;
	public static int[][] field;
	public static int ilen, jlen;
	public static int curi, curj, dir;
	
	public static final int[] comi = {-1, 0, 1, 0};
	public static final int[] comj = {0, 1, 0, -1};
	
	public static int flen;
	public static Function[] functions;
	
	public static Queue queue;
	public static Stack stack;
	
	public static void input(Scanner cin)
	{
		ilen = cin.nextInt(); jlen = cin.nextInt();
		field = new int[ilen][jlen];
		
		for (int i = 0; i < ilen; i++)
		{
			for (int j = 0; j < jlen; j++)
			{
				field[i][j] = cin.nextInt();
			}
		}
		
		flen = cin.nextInt();
		functions = new Function[flen+1];
		
	/// if (functions[0] == null) System.out.println("DA");
		
		for (int i = 1; i <= flen; i++)
		{
			functions[i] = new Function(cin.nextInt());
			functions[i].readInput(cin);
		}
		
		curi = cin.nextInt(); curj = cin.nextInt();
		curi = ilen - 1 - curi;// curj = jlen - curj;
		
		dir = cin.nextInt(); lim = cin.nextInt();
		
		stack = new Stack(new StackNode(-1, -1));
		stack.push(new StackNode(1, 0));
		queue = new Queue(new QueueNode(null));
	}
	
	public static boolean processCommand(int funi, int cmdi)
	{
		String cmd = functions[funi].commands[cmdi];
		
		/**
		System.out.println(cmd + " " + lim);
		System.out.println(ilen - 1 - curi + " " + curj + " " + dir);
		**/
		
		if (cmd.equals("FWD"))
		{
			lim--;
			
			if (curi + comi[dir] >= 0 && curi + comi[dir] < ilen && curj + comj[dir] >= 0 && curj + comj[dir] < jlen && field[curi+comi[dir]][curj+comj[dir]] == 0)
			{
				curi += comi[dir]; curj += comj[dir];
				return true;
			}
			
			return false;
		}
		else if (cmd.equals("RGT"))
		{
			dir = (dir + 1) % 4;
			lim--;
		}
		else if (cmd.equals("LFT"))
		{
			dir = (dir - 1 + 4) % 4;
			lim--;
		}
		/**
		else if (cmd.equals("FUN"))
		{
			stack.push(new StackNode(funi, cmdi + 1));
		}
		**/
		else if (cmd.equals("SETJMP"))
		{			
			Stack newStack = stack.copy().copy();
			newStack.push(new StackNode(funi, cmdi + 1));
		/// newStack.print();
			queue.push(new QueueNode(newStack));
			/**
			System.out.print("Queue stack at push() ");
			queue.front().callStack.print();
			**/
		}
		else if (cmd.equals("JMP"))
		{
			if (queue.empty())
			{
			/// lim--;
				return false;
			}
			
			/**
			System.out.print("Queue stack at front() ");
			queue.front().callStack.print();
			**/
			
		/// System.out.println("DBG2: " + queue.front().callStack.top().funIdx + " " + queue.front().callStack.top().cmdIdx);
			
			stack = queue.front().callStack;
			queue.pop();
		}
		
		return true;
	}
	
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		input(cin);
		
		while (!stack.empty())
		{
			StackNode cur = stack.top();
			stack.pop();
						
			for (int i = cur.cmdIdx; i < functions[cur.funIdx].length && lim > 0; i++)
			{
				/**
				if (queue.front() != null) {
					System.out.print("Casual queue debug ");
					queue.front().callStack.print();
				}
				**/
				
				System.out.println(functions[cur.funIdx].commands[i]);
				
				if (functions[cur.funIdx].commands[i].indexOf("FUN") == 0)
				{
				/// System.out.println(functions[cur.funIdx].commands[i] + " " + lim);
					stack.push(new StackNode(cur.funIdx, i + 1));
					stack.push(new StackNode(Integer.valueOf(functions[cur.funIdx].commands[i].substring(4)), 0));
					break;
				}
				
				if (!processCommand(cur.funIdx, i))
				{
					break;
				}
			}
		}
		
		System.out.printf("%d %d %d\n", ilen - 1 - curi, curj, dir);
		
		/**
		for (int i = 1; i <= flen; i++)
		{
			System.out.printf("FUNCTION %d\n", i);
			
			for (int j = 0; j < functions[i].length; j++)
			{
				System.out.printf("%s\n", functions[i].commands[j]);
			}
			
			System.out.println();
		}
		**/
	}
}

class Function
{
	public int length;
	public String[] commands;
	
	public Function(int a)
	{
		this.length = a;
		this.commands = new String[a];
	}
	
	public void readInput(Scanner cin)
	{
		for (int i = 0; i < this.length;)
		{
			String cmd = cin.nextLine();
			
			if (cmd.length() == 0) continue;
			
			this.commands[i++] = cmd;
		}
	}
}

class QueueNode
{
	public Stack callStack;
	public QueueNode next;
	
	public QueueNode(Stack a)
	{
		this.callStack = a;
		this.next = null;
	}
}

class Queue
{
	public QueueNode begin;
	public QueueNode end;
	
	public Queue(QueueNode a)
	{
		this.begin = a;
		this.end = null;
	}
	
	public void push(QueueNode a)
	{
		if (this.begin.next == null || this.end == null)
		{
			this.begin.next = this.end = a;
		}
		else
		{
			this.end.next = a;
			this.end = a;
		}
	}
	
	public void pop()
	{
		if (!empty())
			this.begin.next = this.begin.next.next;
	}
	
	public QueueNode front()
	{
		return this.begin.next;
	}
	
	public boolean empty()
	{
		return this.begin.next == null;
	}
}

class StackNode
{
	public int funIdx;
	public int cmdIdx;
	public StackNode next;
	
	public StackNode(int a, int b)
	{
		this.funIdx = a;
		this.cmdIdx = b;
		this.next = null;
	}
}

class Stack
{
	public StackNode begin;
	
	public Stack(StackNode a)
	{
		this.begin = a;
	}
	
	public void push(StackNode a)
	{
		a.next = this.begin.next;
		this.begin.next = a;
	}
	
	public void pop()
	{
		if (!empty())
			this.begin.next = this.begin.next.next;
	}
	
	public StackNode top()
	{
		return this.begin.next;
	}
	
	public boolean empty()
	{
		return this.begin.next == null;
	}
	
	public Stack copy()
	{
		Stack newStack = new Stack(new StackNode(-1, -1));
		StackNode it = this.begin.next;
		
		while (it != null)
		{
			newStack.push(new StackNode(it.funIdx, it.cmdIdx));
			it = it.next;
		}
		
		return newStack;
	}
	
	public void print()
	{
		StackNode it = this.begin.next;
		System.out.print("Stack = ");
		while (it != null)
		{
			System.out.printf("[%d %d] ", it.funIdx, it.cmdIdx);
			it = it.next;
		}
		
		System.out.println();
	}
}

/**
Queue FIFO: DODAVAM NA KRAJ, ZEMAM OD POCETOK
Stack LIFO: DODAVAM NA POCETOK, ZEMAM OD POCETOK

VO STACKOT TREBA DA CUVAM:
1) KOJA FUNKCIJA E
2) KOJ UKAZ OD FUNKCIJATA
3) PRETHODNITE ELEMENTI NA STACKOT
**/