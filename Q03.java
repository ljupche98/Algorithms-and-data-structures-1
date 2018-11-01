import java.util.*;

public class Q03
{
	private static int maxW;
	private static int length;
	private static DLL list;
	
	public static void input(Scanner cin)
	{
		maxW = cin.nextInt();
		length = cin.nextInt();
		
		list = new DLL(maxW, length);
		
		for (int i = 0; i < length; i++)
		{
			int W = cin.nextInt(), L = cin.nextInt();
			DLLNode N = new DLLNode(W, L);
			N.readCargo(cin);
			list.insert(N);
		}
	}
	
	public static void solve(Scanner cin)
	{
		String cmd;
		
		while (cin.hasNext())
		{
			cmd = cin.next();
			
			if (cmd.equals("STOP")) break;
			
			if (cmd.equals("ODSTRANI_LIHE"))
			{
				list = list.removeOddIndexedElements();
			}
			else if (cmd.equals("ODSTRANI_HET"))
			{
				list = list.removeDifferentTypes(cin.nextInt());
			}
			else if (cmd.equals("ODSTRANI_ZAS"))
			{
				list = list.removePerc(cin.nextInt());
			}
			else if (cmd.equals("OBRNI"))
			{
				list = list.reverse();
			}
			else if (cmd.equals("UREDI"))
			{
				list.begin().setNext(list.sort(list.begin().next()));
				list.updateEnd();
			}
			else if (cmd.equals("PREMAKNI"))
			{
				list.change(cin.next(), cin.nextInt(), cin.nextInt());
			}
			
		/// list.printWagonWeights();
			
			/** TREBA DA SE IZBRISI **/
			/// list.printsol();
		}
	}
	
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		input(cin);
	/// list.printWagonWeights();
		solve(cin);
		cin.close();
		
		/**
		list.printWagonWeights();
		list = list.removeDifferentTypes(5);
		list.printWagonWeights();
		list = list.reverse();
		list.printWagonWeights();
		
		list.begin().setNext(list.sort(list.begin().next()));
		list.setEnd(list.end().next());
		
		list.printWagonWeights();
		
		list.change("AAAAD", 7, 0);
		
		list.printWagonWeights();
		
		System.out.println(list.DLLLen());
		**/
		
		list.printsol();
		
	/// solve();
	/// list.printsol();
		/**
		list.printWeight(list.first());
		list.begin().setNext(list.sort(list.begin().next()));
		**/
		/**
		list.removeOddIdx(list.first());
		list.removeOddIdx(list.first());
		**/
	/// list.removeCargoPerc(list.begin(), 100);		
	/// solve();
	/// list.printWeight(list.first());
	}
}

class DLL
{
	private int maxWeight;
	private int length;
	private DLLNode begin;	/// points at one element before the first element
	private DLLNode end;	/// points at the last element
	
	public DLL(int a, int b)
	{
		this.maxWeight = a;
		this.length = b;
		this.begin = new DLLNode(-1, 0);
		this.end = null;
	}
	
	public void printsol()
	{
		System.out.printf("%d %d\n", this.maxWeight, DLLLen());
		DLLNode it = this.begin.next();
		boolean valid = true;
		int total = 0;
		
		while (it != null)
		{
			System.out.printf("%d %d\n", it.maxWeight(), it.calcDifferent());
			
			int cur = 0;
			
			for (int i = 0; i < it.cargo().length; i++)
			{
				if (it.cargo()[i] != null && (int) it.cargo()[i].second() > 0)
				{
					cur += (int) it.cargo()[i].second();
					System.out.printf("%s %d\n", it.cargo()[i].first(), it.cargo()[i].second());
				}
			}
			
			valid &= (cur <= it.maxWeight());
			total += cur;
			
			it = it.next();
		}
		
		valid &= (total <= this.maxWeight);
		
		if (valid) System.out.println("DA");
		else System.out.println("NE");
	}
	
	public int DLLLen()
	{
		int res = 0;
		DLLNode it = this.begin.next();
		
		while (it != null)
		{
			res++;
			it = it.next();
		}
		
		return res;
	}
	
	public DLLNode nodeAt(int idx)
	{
		DLLNode it = this.begin.next();
		
		while (it != null && idx > 0)
		{
			it = it.next();
			idx--;
		}
		
		return it;
	}
	
	public void change(String name, int f, int t)
	{
		DLLNode from = nodeAt(f), to = nodeAt(t);
		
		if (from == null || to == null) return;
		
		int fidx = -1, tidx = -1;
		
		for (int i = 0; i < from.cargo().length && fidx == -1; i++)
		{
			if (from.cargo()[i] != null && from.cargo()[i].first().equals(name))
			{
				fidx = i;
			}
		}
		
		if (fidx == -1) return;
		
		for (int i = 0; i < to.cargo().length && tidx == -1; i++)
		{
			/** POSLEDNIOT USLOV TREBA DA SE IZBRISI ILI OSTANI ZA PRIMEROT KOGA :  
				DODAVAS 1, GO BRISIS, DODAVAS 2, DODAVAS 1. DALI DA SE KLAJ NA KRAJ ILI KAJ SO BESE? **/
			if (to.cargo()[i] != null && to.cargo()[i].first().equals(name) && (int) to.cargo()[i].second() > 0)
			{
				tidx = i;
			}
		}
		
	/// System.out.println(fidx + " " + tidx);
		
		if (tidx != -1)
		{
			to.cargo()[tidx] = new Pair(to.cargo()[tidx].first(), (int) from.cargo()[fidx].second() + (int) to.cargo()[tidx].second());
			from.cargo()[fidx] = new Pair(from.cargo()[fidx].first(), (int) 0);
			return;
		}
		
		for (int i = 0; i < to.cargo().length; i++)
		{
			if (to.cargo()[i] == null)
			{
				to.cargo()[i] = new Pair(from.cargo()[fidx].first(), from.cargo()[fidx].second());
				from.cargo()[fidx] = new Pair(from.cargo()[fidx].first(), (int) 0);
				return;
			}
		}
	}
	
	public void updateEnd()
	{
		if (this.begin.next() == null)
		{
			this.end = null;
			return;
		}
		
		DLLNode it = this.begin.next();
		
		while (it.next() != null)
		{
			it = it.next();
		}
		
		this.end = it;
	}
	
	/** returns the first node in the second half ... the list is cut in two parts **/
	public DLLNode split(DLLNode start)
	{
		DLLNode itf = start, its = start;
		
		/** itf can't be null ... split won't be called if itf is null **/
		
		while (itf.next() != null && itf.next().next() != null)
		{
			itf = itf.next().next();
			its = its.next();
		}
		
		/**
		System.out.print("ITF: ");
		printWeight(itf);
		
		System.out.print("ITS: ");
		printWeight(its);
		**/
		
		DLLNode res = its.next();
		its.setNext(null);
		res.setPrev(null);
		return res;
	}
	
	public DLLNode merge(DLLNode left, DLLNode right)
	{
		if (left == null) return right;
		if (right == null) return left;
		
		if (left.calcWeight() <= right.calcWeight())
		{
			left.setNext(merge(left.next(), right));
			left.next().setPrev(left);
			left.setPrev(null);
			return left;
		}
		
		right.setNext(merge(left, right.next()));
		right.next().setPrev(right);
		right.setPrev(null);
		return right;
	}
	
	/** MERGE SORT ... O(nlogn) **/
	public DLLNode sort(DLLNode start)
	{
		if (start == null || start.next() == null) return start;
		
		DLLNode rem = split(start);
		
		/**
		System.out.print("LEFT: ");
		printWeight(start);
		
		System.out.print("RIGHT: ");
		printWeight(rem);
		**/
		
		start = sort(start);
		rem = sort(rem);
		
		return merge(start, rem);
	}
	
	public DLL reverse()
	{
		DLL res = new DLL(this.maxWeight, this.length);
		DLLNode it = this.end;
		
		while (it != null)
		{
			DLLNode newNode = new DLLNode(it.maxWeight(), it.length());
			newNode.setCargo(it.cargo());
			res.insert(newNode);
			it = it.prev();
		}
		
		return res;
	}
	
	public DLL removePerc(int perc)
	{
		DLL res = new DLL(this.maxWeight, this.length);
		DLLNode it = this.begin.next();
		
		while (it != null)
		{
			if ((double) ((double) it.calcWeight() * (double) 100) / (double) it.maxWeight() < (double) perc)
			{
				DLLNode newNode = new DLLNode(it.maxWeight(), it.length());
				newNode.setCargo(it.cargo());
				res.insert(newNode);
			}
			
			it = it.next();
		}
		
		return res;
	}
	
	public DLL removeDifferentTypes(int diff)
	{
		DLL res = new DLL(this.maxWeight, this.length);
		DLLNode it = this.begin.next();
		
		while (it != null)
		{
		/// System.out.println("\nDBG: " + it.calcWeight() + " " + it.calcDifferent() + " ... \n");
			if (it.calcDifferent() < diff)
			{
				DLLNode newNode = new DLLNode(it.maxWeight(), it.length());
				newNode.setCargo(it.cargo());
				res.insert(newNode);
			}
			
			it = it.next();
		}
		
		return res;
	}
	
	public DLL removeOddIndexedElements()
	{
		DLL res = new DLL(this.maxWeight, this.length);
		DLLNode it = this.begin.next();
		int cnt = 0;
		
		while (it != null)
		{
			if (cnt % 2 == 0)
			{
				DLLNode newNode = new DLLNode(it.maxWeight(), it.length());
				newNode.setCargo(it.cargo());
				res.insert(newNode);
			}
			
			cnt++;
			it = it.next();
		}
		
		return res;
		
		/**
		
		VOID
		
		this.end = null;
		int counter = 0;
		DLLNode last = this.begin;
		DLLNode it = this.begin.next();
		
		while (it != null)
		{
			if (counter % 2 == 1)
			{
				counter++;
				it = it.next();
				continue;
			}
			
			last.setNext(it);
			it.setPrev(last);
			last = it;
			this.end = it;
			
			counter++;
			it = it.next();
		}
		**/
	}
	
	public void insert(DLLNode a)
	{
		if (this.end == null)
		{
			this.begin.setNext(a);
			this.end = a;
			return;
		}

		this.end.setNext(a);
		a.setPrev(this.end);
		this.end = a;
	}
	
	public void printWagonWeights()
	{
		System.out.println("NOR: ");
		
		DLLNode it = this.begin.next();
		
		while (it != null)
		{
			System.out.printf("%d ", it.calcWeight());
			it = it.next();
		}
		
		System.out.println("\nREV: ");
		
		it = this.end;
		
		while (it != null)
		{
			System.out.printf("%d ", it.calcWeight());
			it = it.prev();
		}
		
		System.out.println();
	}

	public void setEnd(DLLNode a)
	{
		this.end = a;
	}
	
	public DLLNode end()
	{
		return this.end;
	}

	public DLLNode begin()
	{
		return this.begin;
	}
	
	public DLLNode first()
	{
		return this.begin.next();
	}
}

class DLLNode
{
	private int maxWeight;
	private int cargoLen;		/// original length	
	private Pair[] cargo;		/// (String, int, boolean) ... (name, weight, used)	
	private DLLNode next;
	private DLLNode prev;
	private int curWeight; 		/// Won't be used now
	
	public DLLNode(int a, int b)
	{
		this.maxWeight = a;
		this.cargoLen = b;
		this.cargo = new Pair[b+20];
		this.next = this.prev = null;
	}
	
	/** COUNTS TYPES OF CARGO **/
	public int count()
	{
		int res = 0;
		
		for (int i = 0; i < cargo.length; i++)
		{
			if (cargo[i] != null && (int) cargo[i].second() > 0)
			{
				res++;
			}
		}
		
		return res;
	}
	
	/** PRINTS THE RESULT **/
	public void print()
	{
		System.out.printf("%d %d\n", this.maxWeight, count());
		
		for (int i = 0; i < cargo.length; i++)
		{
			if (cargo[i] != null)
			{
				System.out.printf("%s %d\n", cargo[i].first(), (int) cargo[i].second());
			}
		}
	}
	
	/** SWAPS NEXT AND PREV **/
	public void swapNeighbours()
	{
		DLLNode temp = this.next;
		this.next = this.prev;
		this.prev = temp;
	}
	
	/** CALCULATES WAGON WEIGHT **/
	public int calcWeight()
	{
		int res = 0;
		
		for (int i = 0; i < this.cargo.length; i++)
		{
			if (this.cargo[i] != null)
			{
				res += (int) this.cargo[i].second();
			}
		}
		
		return res;
	}
	
	/** READS THE INPUT **/
	public void readCargo(Scanner cin)
	{
		for (int i = 0; i < this.cargoLen; i++)
		{
			this.cargo[i] = new Pair(cin.next(), cin.nextInt());
		}
	}
	
	/** CALCULATES HOW MANY DIFFERENT CARGOS THERE ARE **/
	public int calcDifferent()
	{
		int res = 0;
		
		for (int i = 0; i < this.cargo.length; i++)
		{
			if (this.cargo[i] != null && (int) this.cargo[i].second() > 0)
			{
				res++;
			}
		}
		
	/// System.out.println("\nDBG2: " + res + "\n");
		
		boolean[] vis = new boolean[this.cargo.length];
		
		for (int i = 0; i < this.cargo.length; i++)
		{
			if (this.cargo[i] != null && (int) this.cargo[i].second() > 0 && !vis[i])
			{
				for (int j = i + 1; j < this.cargo.length; j++)
				{
					if (this.cargo[j] != null && (int) this.cargo[j].second() > 0 && !vis[j])
					{
						if (this.cargo[i].first().equals(this.cargo[j].first()))
						{
							res--;
							vis[j] = true;
						}
					}
				}
			}
		}
		
		return res;
	}
	
	public int maxWeight()
	{
		return this.maxWeight;
	}
	
	public int length()
	{
		return this.cargoLen;
	}
	
	public Pair[] cargo()
	{
		return this.cargo;
	}
	
	public void setCargo(Pair[] a)
	{
		this.cargo = a;
	}
	
	public DLLNode next()
	{
		return this.next;
	}
	
	public void setNext(DLLNode a)
	{
		this.next = a;
	}
	
	public DLLNode prev()
	{
		return this.prev;
	}
	
	public void setPrev(DLLNode a)
	{
		this.prev = a;
	}
}

class Pair
{
	private Object first;
	private Object second;
	
	public Pair(Object a, Object b)
	{
		this.first = a;
		this.second = b;
	}
	
	public Object first()
	{
		return this.first;
	}
	
	public void setFirst(Object a)
	{
		this.first = a;
	}
	
	public Object second()
	{
		return this.second;
	}
	
	public void setSecond(Object a)
	{
		this.second = a;
	}
}