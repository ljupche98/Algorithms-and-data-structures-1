import java.util.*;

public class Q02
{
	public static final int maxLen = (1 << 25);
	public static String[] finalCargos;
	
	public static int dif = 0;
	
	public static Queue BFS;
	public static Stack[] cache;
	public static int ilen, jlen;
	public static String[][] start;
	public static String[][] finish;
	
	public static void processInput(String[][] where, int i, int j, String line)
	{
		if (line.indexOf(",") == -1)
		{
			if (line.length() == 0) return;
			
	///		if (where == finish) dif++;
			
			where[i][j] = line;
			return;
		}
		
	///	if (where == finish) dif++;
		
		where[i][j] = line.substring(0, line.indexOf(","));
		
		if (where[i][j].length() == 0)
		{
	///		if (where == finish) dif--;
			
			where[i][j] = null;
		}
		
		processInput(where, i, j + 1, line.substring(line.indexOf(",") + 1));
	}
	
	public static void input(Scanner cin)
	{
		ilen = cin.nextInt(); jlen = cin.nextInt();
		start = new String[ilen][jlen]; finish = new String[ilen][jlen];
		
		for (int i = 0; i < ilen; i++)
		{
			processInput(start, i, 0, cin.next().substring(2));
		}
		
		for (int i = 0; i < ilen; i++)
		{
			processInput(finish, i, 0, cin.next().substring(2));
		}
		
		/*
		int it = 0;
		finalCargos = new String[dif];
		
		for (int i = 0; i < finish.length; i++)
		{
			for (int j = 0; j < finish[i].length; j++)
			{
				if (finish[i][j] != null)
				{
					finalCargos[it++] = new String(finish[i][j]);
				}
			}
		}
		*/
	}
	
	public static boolean compare(String[][] a, String[][] b)
	{
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[i].length; j++)
			{
				if ((a[i][j] == null ^ b[i][j] == null) || a[i][j] != null && b[i][j] != null && !a[i][j].equals(b[i][j]))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static boolean checkSame(QueueNode state)
	{
		for (int i = 0; i < finish.length; i++)
		{
			for (int j = 0; j < finish[i].length; j++)
			{
				if (finish[i][j] != null)
				{
					boolean found = false;
					
					if (state.cargo != null && finish[i][j].equals(state.cargo))
					{
						found = true;
						state.cargo = null;
						continue;
					}
					
					for (int x = 0; x < state.field.length && !found; x++)
					{
						for (int y = 0; y < state.field[x].length; y++)
						{
							if (state.field[x][y] != null)
							{
								if (finish[i][j].equals(state.field[x][y]))
								{
									found = true;
									state.field[x][y] = null;
									break;
								}
							}
						}
					}
					
					if (!found)
					{
						return false;
					}
				}
			}
		}
		
		return true;
		
		/*
		int visited = 0, cnt = 0;
		
		for (int i = 0; i < state.field.length && cnt <= dif; i++)
		{
			for (int j = 0; j < state.field[i].length && cnt <= dif; j++)
			{
				if (state.field[i][j] != null)
				{
					boolean found = false;
					
					for (int k = 0; k < dif && cnt <= dif; k++)
					{
						if ((visited & (1 << k)) == 0)
						{
							if (state.field[i][j].equals(finalCargos[k]))
							{
								visited |= (1 << k);
								cnt++;
								found = true;
								break;
							}
							else if (state.cargo != null && state.cargo.equals(finalCargos[k]))
							{
								visited |= (1 << (dif + 1));
								cnt++;
								found = true;
								break;
							}
						}
					}
					
					if (!found) return false;
				}
			}
		}
		
		if (cnt + (state.cargo != null ? 1 : 0) >= dif) return true;
		
		return false;
		*/
	}
	
	public static boolean empty(String[] a)
	{
		for (int i = 0; i < a.length; i++)
			if (a[i] != null)
				return false;

		return true;
	}
	
	public static int extend(int hash)
	{
		hash = Math.abs(hash);
		
		if (hash >= maxLen) return hash % maxLen;

		return hash;
	}

	public static void solve()
	{
		cache = new Stack[maxLen];
		BFS = new Queue(new QueueNode(start));
		
		/*for (int i = 0; i < finalCargos.length; i++) System.out.println(finalCargos[i]);
		if (true) return;*/
		
		for (int i = 0; i < ilen; i++)
		{
			QueueNode firstMove = new QueueNode(start);
			firstMove.idx = i;
			firstMove.lastCmd = "PREMIK";
			firstMove.cargo = null;
			firstMove.res += "PREMIK " + (i + 1) + "\n";
			BFS.push(firstMove);
		}
		
		while (!BFS.empty())
		{
			QueueNode cur = BFS.front();
			BFS.pop();
			
			if (compare(cur.field, finish))
			{
				System.out.print(cur.res);
				break;
			}
			
		//	System.out.println(cur.res);
			
			String curCache = cur.cacheString();
			int hashIdx = extend(curCache.hashCode());
			
			if (cache[hashIdx] != null && cache[hashIdx].find(curCache)) continue;
			
			if (cache[hashIdx] == null) cache[hashIdx] = new Stack(new StackNode(""));
			cache[hashIdx].push(new StackNode(curCache));
			
			if (cur.lastCmd == null || !cur.lastCmd.equals("PREMIK"))
			{
				for (int i = 0; i < ilen; i++)
				{
					if (i == cur.idx) continue;
					
					QueueNode makeMove = cur.copy();
					makeMove.idx = i;
					makeMove.res += "PREMIK " + (i + 1) + "\n";
					BFS.push(makeMove);
				}
			}
			
			if (cur.lastCmd == null || !cur.lastCmd.equals("ODLOZI"))
			{
				if (cur.cargo == null && cur.field[cur.idx][0] != null)
				{
					QueueNode take = cur.copy();
					take.lastCmd = "NALOZI";
					take.cargo = take.field[take.idx][0];
					take.field[take.idx][0] = null;
					take.res += "NALOZI\n";
					BFS.push(take);
				}
			}
			
			if (cur.lastCmd == null || !cur.lastCmd.equals("NALOZI"))
			{
				if (cur.cargo != null && cur.field[cur.idx][0] == null)
				{
					QueueNode put = cur.copy();
					put.lastCmd = "ODLOZI";
					put.field[put.idx][0] = put.cargo;
					put.cargo = null;
					put.res += "ODLOZI\n";
					BFS.push(put);
				}
			}
			
			if (!empty(cur.field[cur.idx]))
			{
				QueueNode up = cur.copy();
				up.res += "GOR\n";
				up.lastCmd = null;
				for (int j = up.field[up.idx].length - 2; j >= 0; j--)
					up.field[up.idx][j+1] = up.field[up.idx][j];
				up.field[up.idx][0] = null;
				
				if (checkSame(up.copy())) BFS.push(up);
			}
			
			if (!empty(cur.field[cur.idx]))
			{
				QueueNode down = cur.copy();
				down.res += "DOL\n";
				down.lastCmd = null;
				for (int j = 0; j < down.field[down.idx].length - 1; j++)
					down.field[down.idx][j] = down.field[down.idx][j+1];
				down.field[down.idx][down.field[down.idx].length-1] = null;
					
				if (checkSame(down.copy())) BFS.push(down);
			}
		}
	}
	
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		input(cin);
		solve();
	}
}

class QueueNode
{
	public int idx;
	public String lastCmd = null;
	public String cargo;
	public String res;
	public String[][] field;
	public QueueNode next;
	
	public QueueNode(String[][] a)
	{
		idx = -1;
		lastCmd = null;
		cargo = null;
		res = "";
		field = a;
		next = null;
	}

	public String cacheString()
	{
		String cachef = new String(idx + " " + cargo);
		
		for (int i = 0; i < field.length; i++)
		{
			for (int j = 0; j < field[i].length; j++)
			{
				if (field[i][j] != null)
				{
					cachef += i + "," + j + field[i][j];
				}
			}
		}
		
	///	System.out.println(cachef + " " + cachef.hashCode());
		
		return cachef;
	}
	
	public QueueNode copy()
	{
		QueueNode newNode = new QueueNode(field);
		newNode.field = new String[field.length][field[0].length];
		
		for (int i = 0; i < field.length; i++)
			for (int j = 0; j < field[i].length; j++)
				if (field[i][j] != null)
					newNode.field[i][j] = new String(field[i][j]);
				
		newNode.idx = idx;
		if (cargo != null) newNode.cargo = new String(cargo);
		newNode.res = new String(res);
		return newNode;
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
	public String cache;
	public StackNode next;
	
	public StackNode(String a)
	{
		cache = a;
		next = null;
	}
}

class Stack
{
	public StackNode begin;
	
	public Stack(StackNode a)
	{
		this.begin = a;
	}
	
	public boolean find(String a)
	{
		StackNode it = this.begin.next;
		
		while (it != null)
		{
			if (it.cache.equals(a)) return true;
			it = it.next;
		}
		
		return false;
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
}