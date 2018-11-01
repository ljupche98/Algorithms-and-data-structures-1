import java.io.*;
import java.util.*;

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

public class Q10
{
	public static boolean STDIO = false;
	
	public static int res = 0x3FFFFFFF;
	public static int[] netVal;
	public static ArrayList<String> nodes = new ArrayList<String>();
	public static ArrayList<Integer> pos = new ArrayList<Integer>();
	public static ArrayList<Integer> neg = new ArrayList<Integer>();
	public static ArrayList<ArrayList<Pair>> graph = new ArrayList<ArrayList<Pair>>();
	public static ArrayList<ArrayList<Pair>> posPartitions = new ArrayList<ArrayList<Pair>>();
	public static ArrayList<ArrayList<Pair>> negPartitions = new ArrayList<ArrayList<Pair>>();
	
	public static void readData(Scanner cin)
	{
		int N = cin.nextInt();
		
		for (int i = 0; i < N; i++)
		{
			String[] edge = cin.next().split(",");
			
			if (graph.size() <= addNode(edge[0])) graph.add(new ArrayList<Pair>());
			
			if (graph.size() <= addNode(edge[1])) graph.add(new ArrayList<Pair>());
			
			graph.get(addNode(edge[0])).add(new Pair(addNode(edge[1]), Integer.valueOf(edge[2])));	/// graph[addNode(edge[0])].pb(mp(addNode(edge[1]), edge[2]));
		}
		
		netVal = new int[nodes.size()];
		
		for (int i = 0; i < graph.size(); i++)
		{
			for (int j = 0; j < graph.get(i).size(); j++)
			{
				netVal[i] += graph.get(i).get(j).second;							/// netVal[i] += graph[i][j].s;
				netVal[graph.get(i).get(j).first] -= graph.get(i).get(j).second;	/// netVal[graph[i][j].f] -= graph[i][j].s;
			}
		}
		
		for (int i = 0; i < netVal.length; i++)
		{
			if (netVal[i] > 0) pos.add(netVal[i]);
			if (netVal[i] < 0) neg.add(Math.abs(netVal[i]));
		}
		
		findPartitions(0, new ArrayList<Integer>(), pos, posPartitions);
		findPartitions(0, new ArrayList<Integer>(), neg, negPartitions);
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
	
	public static void output(String[] args)
	{
		if (STDIO) System.out.println(res);
		else try 
		{
			PrintWriter writer = new PrintWriter(args[1]);
			writer.println(res); writer.flush(); writer.close();
		}
		catch (FileNotFoundException e) {}
	}
	
	public static int addNode(String a)
	{
		int idx = -1;
		
		for (int i = 0; i < nodes.size() && idx == -1; i++)
			if (nodes.get(i).equals(a))
				idx = i;
		
		if (idx == -1)
		{
			idx = nodes.size();
			nodes.add(a);
		}
		
		return idx;
	}
	
	public static int findMaximum(ArrayList<Integer> v)
	{
		int max = 0;
		
		for (int i = 0; i < v.size(); i++)
			max = Math.max(max, v.get(i));
		
		return max;
	}
	
	public static void findPartitions(int maxn, ArrayList<Integer> aux, ArrayList<Integer> data, ArrayList<ArrayList<Pair>> res)
	{
		if (aux.size() == data.size())
		{
			ArrayList<Pair> newv = new ArrayList<Pair>();
			
			for (int i = 0, j = findMaximum(aux) + 1; i < j; i++) newv.add(new Pair(0, 0));
			
			for (int i = 0; i < data.size(); i++)
				newv.set(aux.get(i), new Pair(newv.get(aux.get(i)).first + data.get(i), newv.get(aux.get(i)).second + 1));
			
			Collections.sort(newv, new Comparator<Pair>() {
				@Override
				public int compare(Pair a, Pair b) {
					if (a.first < b.first) return -1;
					if (a.first > b.first) return 1;
					return 0;
				}
			});
			
			res.add(newv);
			
			return;
		}
		
		for (int i = 0; i < maxn + 1; i++)
		{
			aux.add(i);
			findPartitions(Math.max(maxn, i + 1), aux, data, res);
			aux.remove(aux.size() - 1);	/// aux.pop_back();
		}
	}
	
	public static boolean ale(ArrayList<Pair> a, ArrayList<Pair> b)
	{
		if (a.size() != b.size()) return false;
		
		for (int i = 0; i < a.size(); i++)
			if (a.get(i).first != b.get(i).first)
				return false;
			
		return true;
	}
	
	public static void main(String[] args)
	{
		input(args);
		
		for (ArrayList<Pair> it : posPartitions)
		{
			for (ArrayList<Pair> in : negPartitions)
			{
				if (ale(it, in))
				{
					int cur = 0;
					
					for (int i = 0; i < it.size(); i++)
						cur += it.get(i).second + in.get(i).second - 1;
					
					res = Math.min(res, cur);
				}
			}
		}
		
		output(args);
	}
}