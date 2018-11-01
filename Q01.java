import java.util.*;

public class Q01
{
	private static int N;
	private static int length;
	private static int index = 0;
	private static Object cur;
/// private static Deque boxes = new Deque(10000);
	private static Deque[] state = new Deque[11];
	
	public static void input(Scanner cin)
	{
	/// Scanner cin = new Scanner(System.in);
		N = cin.nextInt();
		length = cin.nextInt();
		
		state[0] = new Deque(10000);
		
		for (int i = 1; i <= N; i++)
			state[i] = new Deque(length);
		
		String box = cin.next();
		while (box.length() > 0)
		{
			if (box.indexOf(',') != -1)
			{
				state[0].push_back(box.substring(0, box.indexOf(',')));
				box = box.substring(box.indexOf(',') + 1, box.length());
			}
			else break;			
		}
		
		state[0].push_back(box);
	}
	
	public static void solve(Scanner cin)
	{
	/// Scanner cin = new Scanner(System.in);
		
		while (cin.hasNext())
		{
			String cmd = cin.next();
			
			/** TREBA DA GO IZBRISAM **/
			if (cmd.equals("STOP")) break;
			/** TREBA DA GO IZBRISAM **/
			
			if (cmd.equals("PREMIK"))
			{
				index = cin.nextInt();
			}
			else if (cmd.equals("NALOZI"))
			{
				if (cur == null)
				{
					cur = state[index].front();
					state[index].pop_front();
					
					if (index != 0)
					{
						state[index].push_front(null);
					}
				}
			}
			else if (cmd.equals("ODLOZI"))
			{
				if (cur != null && index != 0)
				{
					if (state[index].isEmpty())
					{
						state[index].push_front(cur);
						cur = null;
					}
					else if (state[index].front() == null)
					{
						state[index].pop_front();
						state[index].push_front(cur);
						cur = null;
					}
				}
			}
			else if (cmd.equals("GOR"))
			{
				if (state[index].size() == state[index].length())
				{
					state[index].pop_back();
					state[index].push_front(null);
				}
				else
				{
					state[index].push_front(null);
				}
			}
			else if (cmd.equals("DOL"))
			{
				state[index].pop_front();
			}
			
			/** TREBA DA GO IZBRISAM 
			for (int i = 1; i <= N; i++)
			{
				System.out.printf("%d:", i);
				state[i].print();
				System.out.println();
			}
				TREBA DA GO IZBRISAM **/
		}
		
		for (int i = 1; i <= N; i++)
		{
			System.out.printf("%d:", i);
			state[i].print();
			System.out.println();
		}
	}
	
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		input(cin);
		solve(cin);
	
		/**
		Deque test = new Deque(10);
		
		for (int i = 1; i <= 15; i++) test.push_back(Integer.toString(i));
		
		for (int i = 1; i <= 5; i++) test.pop_back();
		
		test.print();
		**/
	}
}

class Deque
{
	private int size;				/// the current size of the deque
	private int length;				/// the maximum length of the deque
	private int begin;				///	index of the first element in the array; is -1 if the deque is empty
	private int end;				/// index of the last element in the array; is -1 if the deque is empty
	private int nonNull;			/// (element count) # of non-null elements in the array
	private Object[] elements;		/// an array containing the elements
	
	public Deque(int len)
	{
		this.size = 0;
		this.length = len;
		this.begin = this.end = -1;
		this.nonNull = 0;
		this.elements = new Object[len];
	}
	
	public void print()
	{
		for (int i = 0, t = this.nonNull; i < this.size && t != 0; i++)
		{
			if (i > 0)
			{
				System.out.print(",");
			}
			
			if (this.elements[(this.begin+i)%this.length] != null)
			{
				System.out.print(this.elements[(this.begin+i)%this.length]);
				t--;
			}
		}
	}
	
	public boolean isEmpty()
	{
		return this.size == 0;
	}
	
	public Object front()
	{
		if (this.size > 0)
		{
			return this.elements[this.begin];
		}
		
		return null;
	}
	
	public Object back()
	{
		if (this.size > 0)
		{
			return this.elements[this.end];
		}
		
		return null;
	}
	
	public void push_front(Object a)
	{
		if (this.size == 0)
		{
			this.elements[0] = a;
			this.begin = this.end = 0;
			this.size++;
			
			if (a != null)
			{
				this.nonNull++;
			}
			
			return;
		}
		
		if (this.size != this.length)
		{
			this.elements[(this.begin-1+this.length)%this.length] = a;
			this.size++;
			this.begin = (this.begin - 1 + this.length) % this.length;
			
			if (a != null)
			{
				this.nonNull++;
			}
		}
	}
	
	public void pop_front()
	{
		if (this.size != 0)
		{
			if (this.elements[this.begin] != null)
			{
				this.nonNull--;
			}
			
			this.begin = (this.begin + 1) % this.length;
			this.size--;
			
			if (this.size == 0)
			{
				this.begin = this.end = -1;
			}
		}
	}
	
	public void push_back(Object a)
	{
		if (this.size == 0)
		{
			this.elements[0] = a;
			this.begin = this.end = 0;
			this.size++;
			
			if (a != null)
			{
				this.nonNull++;
			}
			
			return;
		}
		
		if (this.size != this.length)
		{
			this.elements[(this.end+1)%this.length] = a;
			this.size++;
			this.end = (this.end + 1) % this.length;
			
			if (a != null)
			{
				this.nonNull++;
			}
		}
	}
	
	public void pop_back()
	{
		if (this.size != 0)
		{
			if (this.elements[this.end] != null)
			{
				this.nonNull--;
			}
			
			this.end = (this.end - 1 + this.length) % this.length;
			this.size--;
			
			if (this.size == 0)
			{
				this.begin = this.end = -1;
			}
		}
	}
	
	public int size()
	{
		return this.size;
	}
	
	public int length()
	{
		return this.length;
	}
	
	public int begin()
	{
		return this.begin;
	}
	
	public int end()
	{
		return this.end;
	}
	
	public int elementCount()
	{
		return this.nonNull;
	}
}