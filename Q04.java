import java.util.*;

public class Q04
{
	public static int N;
	public static int maxw;
	public static Pair[] cargo;
	public static Queue group;
	public static int gmin = 0x3FFFFFFF;
	public static final int maxLen = (1 << 25);
	public static int[] cache = new int[maxLen];
	
	public static void input(Scanner cin)
	{
		N = cin.nextInt(); maxw = cin.nextInt();
		cargo = new Pair[N];
		
		for (int i = 0; i < N; i++)
			cargo[i] = new Pair(cin.next(), cin.nextInt());
	}
	
	public static void sort()
	{
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < i; j++)
			{
				if (cargo[i].second > cargo[j].second)
				{
					Pair temp = cargo[i];
					cargo[i] = cargo[j];
					cargo[j] = temp;
				}
			}
		}
	}
	
	public static int bit_count(int mask)
	{
		int res = 0;
		
		for (int i = 0; i < N; i++)
			if ((mask & (1 << i)) != 0)
				res++;

		return res;
	}
	
	public static int calcPrice(int mask)
	{
		int dif = 0;
		
		for (int i = 0; i < N; i++)
		{
			if ((mask & (1 << i)) == 0) continue;
			
			boolean found = false;
			
			for (int j = 0; j < i && !found; j++)
				if ((mask & (1 << j)) != 0)
					if (cargo[i].first.equals(cargo[j].first))
						found = true;
			
			if (!found)
				dif++;
		}
		
		return 1 + dif;
	}
	
	public static int calcRem(int visited)
	{
		int sum = 0;
		
		for (int i = 0; i < N; i++)
			if ((visited & (1 << i)) == 0)
				sum += cargo[i].second;
		
		return 2 * ((sum / maxw) + (sum % maxw != 0 ? 1 : 0));
	}
	
	public static void findGroups(Queue queue, int idx, int visited, int taken, int rem)
	{
		if (rem <= 0) return;
		
		for (int i = idx; i < N; i++)
		{
			if ((visited & (1 << i)) == 0)
			{
				if (rem - cargo[i].second >= 0)
				{
					queue.push(new QueueNode(taken | (1 << i), calcPrice(taken | (1 << i))));
					findGroups(queue, i + 1, visited, taken | (1 << i), rem - cargo[i].second);
				}
			}
		}
	}
	
	public static int solve(int visited, int curp)
	{
		if (bit_count(visited) == N)
		{
			gmin = Math.min(gmin, curp);
			return 0;
		}
		
		if (visited < maxLen && cache[visited] != 0)
		{
			return cache[visited];
		}
		
		int minr = 0x3FFFFFFF;
		
		for (int i = 0; i < N; i++)
		{
			if ((visited & (1 << i)) == 0)
			{
				Queue group = new Queue(new QueueNode(-1, -1));
				group.push(new QueueNode((1 << i), 2));
				
				findGroups(group, i + 1, visited, (1 << i), maxw - cargo[i].second);
				
				while (!group.empty())
				{
					QueueNode cur = group.front();
					group.pop();
					
					if (curp + cur.value + calcRem(visited | cur.mask) >= gmin) continue;
					
					minr = Math.min(minr, cur.value + solve(visited | cur.mask, curp + cur.value));
				}
				
				break;
			}
		}
		
		if (visited < maxLen)
			return cache[visited] = minr;
		
		return minr;
	}
	
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		input(cin);
		
		sort();
		
		System.out.println(solve(0, 0));
	}
}

class QueueNode
{
	public int mask;
	public int value;
	public QueueNode next;
	
	public QueueNode(int a, int b)
	{
		this.mask = a;
		this.value = b;
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
			this.begin.next = this.end = a;
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

class Pair
{
	public String first;
	public int second;
	
	public Pair(String a, int b)
	{
		this.first = a;
		this.second = b;
	}
}