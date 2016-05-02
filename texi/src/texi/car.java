package texi;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by DESTR on 2016/4/20.
 */
public class car
{
	private String status;
	private int reputation;
	private int stop_count;
	private int wait_count;
	private int _x;
	private int _y;
	private int dest_x;
	private int dest_y;
	private int passenger_x;
	private int passenger_y;
	private Vector<Pair<Integer, Integer>> path;
	private int path_iter;
	private map _map;
	private boolean stopped2serving;

//	public int getStop_count()
//	{
//		return this.stop_count;
//	}
//
//	public int getWait_count()
//	{
//		return this.wait_count;
//	}

	public int getReputation()
	{
		return this.reputation;
	}

	public void choose(int x1, int y1, int x2, int y2)
	{
		this.status = status_kinds.to_passerger;
		this.dest_x = x2;
		this.dest_y = y2;
		this.passenger_x = x1;
		this.passenger_y = y1;
		find_shortest_path(_x, _y, x1, y1);
	}

	public void reputation_up(int x)
	{
		if (x == 1)
			this.reputation += 1;
		else if (x == 3)
			this.reputation += 3;
	}

	public int get_x()
	{
		return _x;
	}

	public int get_y()
	{
		return _y;
	}

	public String getStatus()
	{
		return status;
	}

	public car(map _map)
	{
		this.status = status_kinds.waiting;
		this.reputation = 0;
		this.stop_count = 0;
		this.wait_count = 0;
		path = new Vector<Pair<Integer, Integer>>();
		this._x = new Random().nextInt(80);
		this._y = new Random().nextInt(80);
		this.path_iter = -1;    //path的指针，小于0时做flag：-1:一般/-2:
		this._map = _map;
		this.stopped2serving = false;
	}

	public void move()
	{
		switch (this.status)
		{
			case status_kinds.serving:
			{
//				System.out.println(status+" "+_x+" "+_y+" "+this.reputation);
				if(path.size()!=0)
				{
					_x = path.get(path_iter).getKey();
					_y = path.get(path_iter).getValue();
					path_iter++;
				}

				break;
			}
			case status_kinds.stopped:
			{
//				this.stop_count++;
				break;
			}
			case status_kinds.waiting:
			{
				boolean[] bo_temp = new boolean[4];
				for (int i = 0; i < 4; i++)
					bo_temp[i] = false;

				int[] temp_x = new int[]{_x - 1, _x + 1, _x, _x};
				int[] temp_y = new int[]{_y, _y, _y - 1, _y + 1};

				if (_map.is_up_connected(_x, _y))
					bo_temp[0] = true;
				if (_map.is_down_connected(_x, _y))
					bo_temp[1] = true;
				if (_map.is_left_connected(_x, _y))
					bo_temp[2] = true;
				if (_map.is_right_connected(_x, _y))
					bo_temp[3] = true;

				int rand = new Random().nextInt(4);
				while (bo_temp[rand] == false)
					rand = new Random().nextInt(4);

				this._x = temp_x[rand];
				this._y = temp_y[rand];

//				this.wait_count++;
				break;
			}
			case status_kinds.to_passerger:
			{
//				System.out.println(status+" "+_x+" "+_y+" "+this.reputation);
				if(path.size()!=0)
				{
					_x = path.get(path_iter).getKey();
					_y = path.get(path_iter).getValue();
					path_iter++;
				}

				break;
			}
			default:
		}
	}

	public void update_status()
	{
		switch (this.status)
		{
			case status_kinds.serving:
			{
				if (this._x == this.dest_x && this._y == this.dest_y)
				{
					this.stop_count = 0;
					this.status = status_kinds.stopped;
					reputation_up(3);
				}

				break;
			}
			case status_kinds.stopped:
			{
				this.stop_count++;
				if (this.stop_count >= 10)
				{
					this.stop_count = 0;
					if (this.stopped2serving)
					{
						this.status = status_kinds.serving;
						find_shortest_path(_x, _y, dest_x, dest_y);
						this.stopped2serving = false;
					}
					else
					{
						this.status = status_kinds.waiting;
						this.wait_count = 0;
					}
				}

				break;
			}
			case status_kinds.waiting:
			{
				this.wait_count++;
				if (this.wait_count >= 200)
				{
					this.wait_count = 0;
					this.stop_count = 0;
					this.status = status_kinds.stopped;
				}

				break;
			}
			case status_kinds.to_passerger:
			{
				if (this._x == this.passenger_x && this._y == this.passenger_y)
				{
					this.stop_count = 0;
					this.status = status_kinds.stopped;
					this.stopped2serving = true;
				}

				break;
			}
			default:
		}

	}

	private void find_shortest_path(int x1, int y1, int x2, int y2)
	{
		//起点与终点相同
		if (x1 == x2 && y1 == y2)
		{
			this.path_iter = 0;
			this.path.clear();
			return;
		}

		//记录前驱
		int[][] x_prev = new int[80][80];
		int[][] y_prev = new int[80][80];

		boolean[][] bo = new boolean[80][80];   //记录访问过的点，初始false
		for (boolean[] i : bo)
		{
			for (boolean j : i)
				j = false;
		}

		//队列
		LinkedList<Integer> x_list = new LinkedList<Integer>();
		LinkedList<Integer> y_list = new LinkedList<Integer>();
		x_list.addLast(x1);                 //起点入队
		y_list.addLast(y1);
		bo[x1][y1] = true;                  //起点访问过了

		while (!x_list.isEmpty())
		{
			int x = x_list.removeFirst();   //取出队头
			int y = y_list.removeFirst();

			if (this._map.is_left_connected(x, y) && !bo[x][y - 1])     //与左相连
			{
				x_list.addLast(x);          //左边的点入队
				y_list.addLast(y - 1);
				x_prev[x][y - 1] = x;       //记录其前驱
				y_prev[x][y - 1] = y;
				bo[x][y - 1] = true;        //访问过涂成true

				//新入队的是目的地
				if (x == x2 && y - 1 == y2)
				{
					LinkedList<Pair<Integer, Integer>> temp_list = new LinkedList<Pair<Integer, Integer>>();
					temp_list.addLast(new Pair<Integer, Integer>(x2, y2));

					int temp_x = x_prev[x2][y2];
					int temp_y = y_prev[x2][y2];
					while (temp_x != x1 || temp_y != y1)
					{
						temp_list.addLast(new Pair<Integer, Integer>(temp_x, temp_y));
						int temp_temp_x;
						temp_temp_x = x_prev[temp_x][temp_y];
						temp_y = y_prev[temp_x][temp_y];
						temp_x = temp_temp_x;
					}
					this.path_iter = 0;
					this.path.clear();
					while (!temp_list.isEmpty())
						this.path.add(temp_list.removeLast());
					return;
				}
			}
			if (this._map.is_right_connected(x, y) && !bo[x][y + 1])    //右
			{
				x_list.addLast(x);
				y_list.addLast(y + 1);
				x_prev[x][y + 1] = x;
				y_prev[x][y + 1] = y;
				bo[x][y + 1] = true;

				if (x == x2 && y + 1 == y2)
				{
					LinkedList<Pair<Integer, Integer>> temp_list = new LinkedList<Pair<Integer, Integer>>();
					temp_list.addLast(new Pair<Integer, Integer>(x2, y2));

					int temp_x = x_prev[x2][y2];
					int temp_y = y_prev[x2][y2];
					while (temp_x != x1 || temp_y != y1)
					{
						temp_list.addLast(new Pair<Integer, Integer>(temp_x, temp_y));
						int temp_temp_x;
						temp_temp_x = x_prev[temp_x][temp_y];
						temp_y = y_prev[temp_x][temp_y];
						temp_x = temp_temp_x;
					}
					this.path_iter = 0;
					this.path.clear();
					while (!temp_list.isEmpty())
						this.path.add(temp_list.removeLast());
					return;
				}
			}
			if (this._map.is_down_connected(x, y) && !bo[x + 1][y])     //下
			{
				x_list.addLast(x + 1);
				y_list.addLast(y);
				x_prev[x + 1][y] = x;
				y_prev[x + 1][y] = y;
				bo[x + 1][y] = true;


				if (x + 1 == x2 && y == y2)
				{
					LinkedList<Pair<Integer, Integer>> temp_list = new LinkedList<Pair<Integer, Integer>>();
					temp_list.addLast(new Pair<Integer, Integer>(x2, y2));

					int temp_x = x_prev[x2][y2];
					int temp_y = y_prev[x2][y2];
					while (temp_x != x1 || temp_y != y1)
					{
//						System.out.println(temp_x+"\t"+temp_y);
						temp_list.addLast(new Pair<Integer, Integer>(temp_x, temp_y));
						int temp_temp_x;
						temp_temp_x = x_prev[temp_x][temp_y];
						temp_y = y_prev[temp_x][temp_y];
						temp_x = temp_temp_x;
					}
					this.path_iter = 0;
					this.path.clear();
					while (!temp_list.isEmpty())
						this.path.add(temp_list.removeLast());
					return;
				}
			}
			if (this._map.is_up_connected(x, y) && !bo[x - 1][y])       //上
			{
				x_list.addLast(x - 1);
				y_list.addLast(y);
				x_prev[x - 1][y] = x;
				y_prev[x - 1][y] = y;
				bo[x - 1][y] = true;

				if (x - 1 == x2 && y == y2)
				{
					LinkedList<Pair<Integer, Integer>> temp_list = new LinkedList<Pair<Integer, Integer>>();
					temp_list.addLast(new Pair<Integer, Integer>(x2, y2));

					int temp_x = x_prev[x2][y2];
					int temp_y = y_prev[x2][y2];
					while (temp_x != x1 || temp_y != y1)
					{
						temp_list.addLast(new Pair<Integer, Integer>(temp_x, temp_y));
						int temp_temp_x;
						temp_temp_x = x_prev[temp_x][temp_y];
						temp_y = y_prev[temp_x][temp_y];
						temp_x = temp_temp_x;
					}
					this.path_iter = 0;
					this.path.clear();
					while (!temp_list.isEmpty())
						this.path.add(temp_list.removeLast());
					return;
				}
			}
		}
	}

	class status_kinds
	{
		public static final String waiting = "waiting";
		public static final String stopped = "stopped";
		public static final String serving = "serving";
		public static final String to_passerger = "to-passenger";
	}

}
