package texi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by DESTR on 2016/4/20.
 */
public class disp implements Runnable
{
	private Object lock = new Object();
	private car[] cars;
	private map _map;
	private int run_flag;
	private LinkedList<request> requests;
	public final car_watcher watcher = new car_watcher();

	public disp(map _map)
	{
		this._map = _map;
		cars = new car[100];
		for (int i = 0; i < 100; i++)
			cars[i] = new car(this._map);
		this.run_flag = 1;
		requests = new LinkedList<request>();
	}

	public void add_request(int x, int y, int dest_x, int dest_y)
	{
		if (x < 0 || x >= 80)
		{
			System.out.println("请求不符合规则，被忽略");
			return;
		}
		if (y < 0 || y >= 80)
		{
			System.out.println("请求不符合规则，被忽略");
			return;
		}
		if (dest_x < 0 || dest_x >= 80)
		{
			System.out.println("请求不符合规则，被忽略");
			return;
		}
		if (dest_y < 0 || dest_y >= 80)
		{
			System.out.println("请求不符合规则，被忽略");
			return;
		}
		synchronized (lock)
		{
			request req = new request(x, y, dest_x, dest_y, this);
			requests.addLast(req);

			if (requests.size() > 300)
			{
				System.out.println("程序正在执行的请求数已经超过作业要求的300，虽然程序继续运行，但不对发生的错误负责");
			}

			new Thread(req).start();
		}
	}

	public void complete_req(request req)
	{
		synchronized (lock)
		{
			//从队列中删除
			requests.remove(req);
			//选车
			//先找出信誉最大值
			int max_repu = -1;
			for (int i = 0; i < 100; i++)
			{
				if (cars[i].getStatus() == car.status_kinds.waiting &&  //状态是waiting
						req.getBo_car(i) &&                             //抢过单
						cars[i].getReputation() > max_repu)             //信誉大于当前最大信誉
				{
					max_repu = cars[i].getReputation();
				}
			}
			//无人响应
			if (max_repu == -1)
			{
				System.out.println("请求  (" + req.get_x() + "," + req.get_y() + ") => (" + req.getDest_x() + "," + req.getDest_y() + ") 没有车能够响应");
				return;
			}

			//将所有信誉等于最大信誉&&能接客的车入队
			Map<Integer, car> car_list = new HashMap<Integer, car>();
			for (int i = 0; i < 100; i++)
			{
				if (cars[i].getStatus() == car.status_kinds.waiting &&  //状态是waiting
						req.getBo_car(i) &&                             //抢过单
						cars[i].getReputation() == max_repu)            //信誉大于当前最大信誉
				{
					car_list.put(i, cars[i]);
				}
			}

			//选车
			//只有一车
			if (car_list.size() == 1)
			{
				for (int i : car_list.keySet())
				{
					cars[i].choose(req.get_x(), req.get_y(), req.getDest_x(), req.getDest_y());
					return;
				}
			}
			//多车选最近
			else if (car_list.size() > 1)
			{
				int x1 = req.get_x();
				int y1 = req.get_y();

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
					//找到车
					for (int i : car_list.keySet())
					{
						if (car_list.get(i).get_x() == x && car_list.get(i).get_y() == y)
						{
							cars[i].choose(req.get_x(), req.get_y(), req.getDest_x(), req.getDest_y());
							return;
						}
					}

					if (this._map.is_left_connected(x, y) && !bo[x][y - 1])     //与左相连
					{
						x_list.addLast(x);          //左边的点入队
						y_list.addLast(y - 1);
						bo[x][y - 1] = true;        //访问过涂成true
					}
					if (this._map.is_right_connected(x, y) && !bo[x][y + 1])    //右
					{
						x_list.addLast(x);
						y_list.addLast(y + 1);
						bo[x][y + 1] = true;
					}
					if (this._map.is_down_connected(x, y) && !bo[x + 1][y])     //下
					{
						x_list.addLast(x + 1);
						y_list.addLast(y);
						bo[x + 1][y] = true;
					}
					if (this._map.is_up_connected(x, y) && !bo[x - 1][y])       //上
					{
						x_list.addLast(x - 1);
						y_list.addLast(y);
						bo[x - 1][y] = true;
					}
				}
				System.out.println("请求  (" + req.get_x() + "," + req.get_y() + ") => (" + req.getDest_x() + "," + req.getDest_y() + ") 没有车能够响应");
				return;

			}
			else
			{
				System.out.println("出现了蜜汁问题");
				System.exit(0);
			}
		}
	}

	@Override
	public void run()
	{
		while (run_flag == 1)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			synchronized (lock)
			{
				for (int i = 0; i < 100; i++)
				{
					cars[i].move();
					cars[i].update_status();
				}
//			System.out.println(cars[0].getStatus() + "\t" + cars[0].get_x() + " " + cars[0].get_y());
				//向车门广播请求
				for (request i : requests)
				{
					for (int j = 0; j < 100; j++)
					{
						//在4*4的范围内
						if (cars[j].get_x() >= i.get_x() - 2 && cars[j].get_x() <= i.get_x() + 2 &&
								cars[j].get_y() >= i.get_y() - 2 && cars[j].get_y() <= i.get_y() + 2)
						{
							//没有抢过单
							if (i.getBo_car(j) == false && cars[j].getStatus() == car.status_kinds.waiting)
							{
//								System.out.println(cars[j].get_x() + " " + cars[j].get_y());
								i.setBo_car(j);
								cars[j].reputation_up(1);
							}
						}
					}
				}
			}
		}
	}

	public class car_watcher
	{
		private long clock_0;

		public car_watcher()
		{
			clock_0 = System.currentTimeMillis();
		}

		public int get_x(int x)
		{
			if (x >= 100 || x < 0)
			{
				System.out.println("错误的参数");
				return -1;
			}
			return cars[x].get_x();
		}

		public int get_y(int x)
		{
			if (x >= 100 || x < 0)
			{
				System.out.println("错误的参数");
				return -1;
			}
			return cars[x].get_y();
		}

		public int get_reputation(int x)
		{
			if (x >= 100 || x < 0)
			{
				System.out.println("错误的参数");
				return -1;
			}
			return cars[x].getReputation();
		}

		public String get_status(int x)
		{
			if (x >= 100 || x < 0)
			{
				System.out.println("错误的参数");
				return "WTH!!???";
			}
			return cars[x].getStatus();
		}

		public long get_time()
		{
			return System.currentTimeMillis() - clock_0;
		}
	}
}
