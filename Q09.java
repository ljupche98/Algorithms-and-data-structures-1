import java.io.*;
import java.util.*;

/**
5 8
1 4
2 1
2 3
2 4
3 4
5 1
5 3
5 4
**/

public class Q09
{
	public static final boolean STDIO = true;
	
	public static String res;
	public static int N, M, H;
	public static int[] indeg, heap;
	public static boolean[] visited;
	public static ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
/// public static ArrayList<ArrayList<Integer>> revgr = new ArrayList<ArrayList<Integer>>();
///	public static Stack<Integer> res = new Stack<Integer>(), tsorted = new Stack<Integer>();
	
	public static Stack<Integer> tres = new Stack<Integer>();
	
	public static void readData(Scanner cin)
	{		
		N = cin.nextInt(); M = cin.nextInt();
		
		res = new String(""); indeg = new int[N + 1]; heap = new int[N + 1]; visited = new boolean[N + 1];
		
		for (int i = 0; i <= N; i++, graph.add(new ArrayList<Integer>())); /// , revgr.add(new ArrayList<Integer>()));
		
		for (int i = 0; i < M; i++)
		{
			String[] line = cin.next().split(",");
			graph.get(Integer.valueOf(line[1])).add(Integer.valueOf(line[0]));
			indeg[Integer.valueOf(line[0])]++;
		/// revgr.get(Integer.valueOf(line[1])).add(Integer.valueOf(line[0]));
		}
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
		if (STDIO) System.out.println(res); /// while (!res.empty()) System.out.print(res.pop() + ((--N == 0) ? "\n" : ","));
		else try 
		{
			PrintWriter writer = new PrintWriter(args[1]);
			writer.println(res); /// while (!res.empty()) writer.print(res.pop() + ((--N == 0) ? "\n" : ","));
			writer.flush(); writer.close();
		}
		catch (FileNotFoundException e) {}
	}
	
	public static void printGraph()
	{
		for (int i = 1; i < graph.size(); i++) {
			System.out.printf("%d: ", i);
			for (int j = 0; j < graph.get(i).size(); j++) {
				System.out.printf("%d ", graph.get(i).get(j));
			}
			System.out.printf("\n");
		}
	}
	
	public static int minIdx(int fidx, int sidx)
	{
		if (sidx > H || heap[fidx] <= heap[sidx]) return fidx;
		return sidx;
	}
	
	public static void add(int cur)
	{
		int parent = ++H / 2, idx = H;
	 
		while (parent != 0 && heap[parent] > cur)
		{
			heap[idx] = heap[parent];
			idx = parent;
			parent /= 2;
		}
	 
		heap[idx] = cur;
	}
	
	public static void heapify()
	{
		int idx = 1;
	 
		while (2 * idx <= H && (heap[idx] > heap[minIdx(2 * idx, 2 * idx + 1)]))
		{
			int midx = minIdx(2 * idx, 2 * idx + 1);
			int temp = heap[idx];
			heap[idx] = heap[midx];
			heap[midx] = temp;
		/// swap(heap[idx], heap[midx]);
			idx = midx;
		}
	}
	
	public static void remove()
	{
		heap[1] = heap[H--];
		heapify();
	}
	
	/**
	
	public static void DFS(int idx)
	{
		visited[idx] = true;
		
		for (int i = 0; i < graph.get(idx).size(); i++)
			if (!visited[graph.get(idx).get(i)])
				DFS(graph.get(idx).get(i));
		
		res.push(idx);
		tres.push(idx);
		tsorted.push(idx);
	}
	
	public static void checkSolution()
	{
		for (int i = 1; i <= N; i++) visited[i] = false;
		
		while (!tres.empty())
		{
			int cur = tres.pop();
			
			for (int i = 0; i < revgr.get(cur).size(); i++) {
				if (!visited[revgr.get(cur).get(i)]) {
					System.out.println("NE");
					return;
				}
			}
			
			visited[cur] = true;
		}
		
		System.out.println("DA");
	}
	**/
	
	public static void main(String[] args)
	{
		input(args);
		
		for (int i = 1; i <= N; i++)
			if (indeg[i] == 0)
				add(i);
		
		while (N-- > 0)
		{
			if (H == 0) {res = "-1"; break;}
			
			int idx = heap[1];
			remove();
			
			res += Integer.toString(idx) + (N != 0 ? "," : "");
			
			for (int i = 0; i < graph.get(idx).size(); i++)
				if (--indeg[graph.get(idx).get(i)] == 0)
					add(graph.get(idx).get(i));
		}
		
		output(args);
		
	/// System.out.println(res);
		
		/**
		
		for (int i = 1; i <= N; i++)
			if (!visited[i])
				DFS(i);
			
		for (int i = 1; i <= N; i++) visited[i] = false;
			
		while (!tsorted.empty())
		{
			int scc = 0;
			
			Queue<Integer> BFS = new LinkedList<Integer>();
			BFS.add(tsorted.pop());
			
			if (visited[BFS.element()]) continue;
			
			while (BFS.peek() != null)
			{
				scc++;
				
				int cur = BFS.poll();
				
				if (visited[cur]) continue;
				
				visited[cur] = true;
				
				for (int i = 0; i < revgr.get(cur).size(); i++)
					if (!visited[revgr.get(cur).get(i)])
						BFS.add(revgr.get(cur).get(i));
			}
			
			if (scc > 1)
			{
			/// System.out.println(-1);
				res = new Stack<Integer>();
				res.push(-1);
				N = 1;
				break;
			}
		}
		
		**/
		
	/// output(args);
		
	/// checkSolution();
	}
}