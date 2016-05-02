package texi;

/**
 * Created by DESTR on 2016/4/20.
 */
public class request implements Runnable
{
	private int _x;
	private int _y;
	private int dest_x;
	private int dest_y;
	private disp _disp;
	private boolean[] bo_car;

	public boolean getBo_car(int x)
	{
		return bo_car[x];
	}

	public void setBo_car(int x)
	{
		this.bo_car[x] = true;
	}

	public int get_x()
	{
		return _x;
	}

	public int get_y()
	{
		return _y;
	}

	public int getDest_x()
	{
		return dest_x;
	}

	public int getDest_y()
	{
		return dest_y;
	}

	public request(int _x, int _y, int dest_x, int dest_y, disp _disp)
	{
		this._x = _x;
		this._y = _y;
		this.dest_x = dest_x;
		this.dest_y = dest_y;
		this.bo_car = new boolean[100];
		for (int i = 0; i < 100; i++)
			this.bo_car[i] = false;
		this._disp = _disp;
	}


	@Override
	public void run()
	{
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		_disp.complete_req(this);
	}
}
