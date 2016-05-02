package texi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DESTR on 2016/4/20.
 */
public class map
{
	private point map[][];
	public boolean is_up_connected(int x,int y)
	{
		return this.map[x][y].up_connected;
	}
	public boolean is_down_connected(int x,int y)
	{
		return this.map[x][y].down_connected;
	}
	public boolean is_left_connected(int x,int y)
	{
		return this.map[x][y].left_connected;
	}
	public boolean is_right_connected(int x,int y)
	{
		return this.map[x][y].right_connected;
	}

	public map()
	{
		this.map = new point[82][82];
		for (int i = 0; i < 80; i++)
			for (int j = 0; j < 80; j++)
				this.map[i][j] = new point();

	}

	public boolean read_file(String path)
	{
		try
		{
			File file_temp = new File(path);
			if (file_temp.exists() && file_temp.isFile())
			{
				FileReader fr = new FileReader(file_temp);
				BufferedReader br = new BufferedReader(fr);

				String str_temp = new String("");
				for (int i = 0; i < 80; i++)
				{
					str_temp = br.readLine();

					if (str_temp == null)
					{
						System.out.println("文件中内容不符合要求");
						return false;
					}

					String pt = "^[0-9]{80,80}$";
					Matcher mt = Pattern.compile(pt).matcher(str_temp);
					if (!mt.find())
					{
						System.out.println("文件中内容不符合要求");
						return false;
					}
					else
					{
						for (int j = 0; j < 80; j++)
						{
							try
							{
//								System.out.println(this.map[i][j].getX());
								this.map[i][j].x = Integer.parseInt(str_temp.substring(j, j + 1));
							}
							catch (Exception e)
							{
//								e.printStackTrace();
							}
						}
					}
				}


				return true;
			}
			else
			{
				System.out.println("文件不存在或是目录");
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("请输入正确的参数，读取输入文件失败");
			return false;
		}
	}

	public void init()
	{
		//初始化图的边
		for (int i = 0; i < 80; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				switch (this.map[i][j].x)
				{
					case 0:
					{
						break;
					}
					case 1:
					{
						if (j + 1 < 80)
						{
							this.map[i][j].right_connected = true;
							this.map[i][j + 1].left_connected = true;
						}
						break;
					}
					case 2:
					{
						if (i + 1 < 80)
						{
							this.map[i][j].down_connected = true;
							this.map[i + 1][j].up_connected = true;
						}
						break;
					}
					case 3:
					{
						if (i + 1 < 80)
						{
							this.map[i + 1][j].up_connected = true;
							this.map[i][j].down_connected = true;
						}
						if (j + 1 < 80)
						{
							this.map[i][j + 1].left_connected = true;
							this.map[i][j].right_connected = true;
						}
						break;
					}
					default:
						break;
				}
			}
		}

	}


	public void print()
	{
		for (int i = 0; i < 80; i++)
		{
			for (int j = 0; j < 80; j++)
			{
				System.out.print(this.map[i][j].x + " ");
			}
			System.out.println();
		}
	}


	class point
	{
		private int x;    //原始值

		//与上下左右是否联通
		private boolean up_connected;
		private boolean down_connected;
		private boolean left_connected;
		private boolean right_connected;

		public point()
		{
			this.x = 0;
			this.up_connected = false;
			this.down_connected = false;
			this.left_connected = false;
			this.right_connected = false;
		}

	}

}
