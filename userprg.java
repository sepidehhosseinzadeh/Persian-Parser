import java.util.*;
import java.io.*;

public class userprg
{

	//gereyeh risheh baraye grammar
	static Node root;
	//hashmap baraye negahdari lexicon ha 
	static HashMap<String, ArrayList<String>> lex_list = new HashMap<String, ArrayList<String>>();
	static HashMap<String, ArrayList<String>> after_lex= new HashMap<String, ArrayList<String>>();
	//tokeinzer
	static ArrayList<String> dictionary = new ArrayList<String>();
	static String[] morphemes = {"خواهم","خواهی","خواهد","خواهیم","خواهید","خواهند","ب","بی","می","نمی","ها","های","ای","ام","شان","تر","تری","ترین","ایست","است","هایی","هایم","هایت","هایش","هایمان","هایتان","هایشان","هاست","هایم","هاید","هایند","ات","اش","مان","تان","ایم","اید","اند"};
	static boolean[] stickBefore = new boolean[morphemes.length];
	static char[] farsiAlphabet = {'ض','ص','ث','ق','ف','غ','ع','ه','خ','ح','ج','چ','پ','ش','س','ی','ب','ل','ا','آ','ت','ن','م','ک','گ','ظ','ط','ز','ژ','ر','ذ','د','ئ','و'};
	static char[] nonFinalForm = {'ا', 'د', 'ذ', 'ر', 'ز', 'ژ', 'و'};
	static boolean[] ambiguous = new boolean[morphemes.length];
	static ArrayList<ArrayList<String>> tokens = new ArrayList<ArrayList<String>>();
	static ArrayList<String> lexiconWords = new ArrayList<String>();


	static class Node
	{
		//class node baraye ijad sakhtar derakhti grammar
		String name;
		String rule;
		int size;
		List<List<Node>> children = new ArrayList<List<Node>>();

		public Node(String Name,String Rule, ArrayList<List<Node>> Children)
		{

			name=Name ;
			rule=Rule;
			children = Children;
			size=0;

		}

		public boolean finder(String des_name,String des_rule )
		{

			//tabeh finder baraye peida kardan makan monAseb har ghanoon az
			//grammar dar derakht .


			ArrayList<Node> father = new ArrayList<Node>();
			father.add(root);
			while(!(father.isEmpty()))
			{

				StringTokenizer step1 = new StringTokenizer(des_rule,"|.->");
				String key = step1.nextToken();

				String a=father.get(0).name;
				a = a.trim();
				key=key.trim();

				if(a.equals(key) )
				{


					if((father.get(0).rule).equals(""))
						father.get(0).rule=des_rule;
					else
						father.get(0).rule=father.get(0).rule+" | "+des_rule;


					int k=father.get(0).size;

					while(step1.hasMoreTokens())
					{
						String childgroup = step1.nextToken();

						StringTokenizer step2 = new StringTokenizer(childgroup);
						while(step2.hasMoreTokens())
						{
							String child = step2.nextToken();

							father.get(0).children.add(new ArrayList<Node>());
							father.get(0).children.get(k).add(new Node(child,"", new ArrayList<List<Node>>()));


						}
						k++;


					}
					father.get(0).size=k;
					return true;


				}
				else
				{
					for(int i=0; i<father.get(0).children.size(); i++)
					{
						for(int j=0; j<father.get(0).children.get(i).size(); j++)
						{

							father.add(father.get(0).children.get(i).get(j));

						}
					}



				}

				father.remove(0);

			}

			return false;
		}

	}

	public static void main_menu()
	{

		//dar in bakhsh karbar be entekhab khod ba yeki az majmooeh haye lexicon 
		//ya gramar kar mikonad .
		Scanner scan = new Scanner(System.in);

		System.out.println("\n"+"\n"+"************************* MAIN MENU *************************"+"\n"+"\n");

		System.out.println("baraye kar ba lexicon ha 1..baraye kar ba grammar 2..baraye tahlil yek reshteye voroodi 3..baraye khorooj 0 ra vared konid:");
		int answer=scan.nextInt();

		switch(answer)
		{
			case 1 :
			{
				lex_menu();
				break;
			}
			case 2 :
			{
				grm_menu();
				break;
			}


			case 3 :
			{
				lexical_analyzer();
				break;
			}

			case 0:
			{
				System.exit(0);
				break;
			}
			default :
			{
				System.out.println("khatA !!!");
				main_menu();
			}


		}
	}



	public static void lexical_analyzer()
	{

		Scanner scan =new Scanner(System.in);
		System.out.println("jomleye mored nazar khod ra vared konid : ");
		String line=scan.nextLine();

		getTokens();

		boolean label =false;
		int size=lex_list.size();
		for (int i=0; i<tokens.size(); i++)
		{
			for (int j=0; j<tokens.get(i).size(); j++)
			{
				String word ="";
				word = tokens.get(i).get(j);
				if(lex_list.containsKey(word))
				{

					label=true;
					String prop = lex_list.get(word);
					after_lex.put(word , prop);
					break;
				}
			}
			if(!label)
			{
				for (int j=0; j<tokens.get(i).size(); j++)
				{


					String word ="";
					word = tokens.get(i).get(j);
					System.out.println("kalameye " + word +" dar list kalamat mojood nist . ");
					Set set = after_lex.entrySet();
					Iterator I = set.iterator();
					while(I.hasNext())
					{

						Map.Entry me = (Map.Entry)I.next();

						String guess = me.getKey();
						int samevalue=dp(word,guess);
						if(samevalue>=-4)
							System.out.println("kalame pishnahadi be jaye  " + word + " : " + guess);


					}




				}


			}



			label=false;

		}

	}


	public static int dp(String a ,String b)

	{
		int s1=a.length();
		int s2=b.length();
		int[][] multi = new int[s1][s2];
		for(int i=0; i<s1; i++)
		{

			for(int j=0; j<=i; j++)
			{
				String first = Character.toString(s1.charAt(i));
				String second = Character.toString(s2.charAt(j));

				if(first.equals(second))
					multi[i][j]=multi[i-1][j-1];
				else
				{

					int v1=multi[i][j-1]-1;
					int v2=multi[i-1][j]-1;
					int v3=multi[i-1][j-1]-2;

					int max=v1;
					if(max<v2)
						max=v2;

					if(max>v3)
						max=v3;

					multi[i][j]=max;

				}


			}


		}

		return multi[s1][s2];

	}


	public static void lex_menu()
	{
		//dar in baksh karbar amali ke mikhahad rooye lexicon ha anjam dahad entekhab mikonad .
		Scanner scan = new Scanner(System.in);

		System.out.println("\n"+"\n"+"************************* LEXICON MENU *************************"+"\n"+"\n");

		System.out.println("adad amal mored nazar khod ra vared konid ");
		System.out.println("1 : ijad majmooei az lexicon ha ");
		System.out.println("2 : viraayesh majmooeye lexicon ha  ");
		System.out.println("3 : hazf lexicon haye mojood  ");
		System.out.println("4 : moshAhedeye majmooeye lexicon ha  ");

		System.out.println("0 : bazgasht be menu ghabl  ");

		int answer=scan.nextInt();
		switch(answer)
		{
			case 1 :
			{
				lex_reader();
				break;
			}
			case 2 :
			{
				lex_editor();
				break;
			}

			case 3 :
			{
				lex_delete();
				break;
			}


			case 4:
			{
				lex_viewer();
				break;
			}



			case 0 :
			{
				main_menu();
				break;
			}

			default :
			{System.out.println("khaTA !!!");
				main_menu();
			}

		}


	}




	public static void lex_reader()
	{



		String word="";
		Scanner scan = new Scanner(System.in);
		try{

			FileReader lexreader = new FileReader("lex.txt");
			BufferedReader br = new BufferedReader(lexreader);

			String s ;

			while((s = br.readLine()) != null )
			{


				ArrayList<String> properties = new ArrayList<String>();
				StringTokenizer line = new StringTokenizer(s,"] [ : ,");
				if(line.hasMoreTokens())
					word=line.nextToken();
				while(line.hasMoreTokens())
				{
					properties.add(line.nextToken());

				}
				if(!word.equals(""))
				{

					lex_list.put(word, properties);
					word="";

				}

			}
			br.close();
			lexreader.close();
			lex_menu();


		}
		catch ( IOException e )
		{

			e.printStackTrace();
		}



	}



	public static void lex_editor()

	{

		Scanner scan = new Scanner(System.in);
		System.out.print("baraye tashih az file shomare 1 va baraye tasHih az terminal shomare 2 ra vared konid  : ");
		int ans=scan.nextInt();
		switch(ans)
		{
			case 1 :
			{
				System.out.println();
				System.out.println("baraye tashih be file lexicon ha beravid va taghirat ra emal konid...sepas end ra vared namaeid ");
				String req=scan.next();

				while(!req.equals("end"))
				{
					req=scan.next();
				}
				lex_list.clear();
				lex_reader();
				break;
			}
			case 2 :
			{


				System.out.println("ghanoon jadid ra vared konid");
				scan.skip("\n");
				String s=scan.nextLine();

				ArrayList<String> properties = new ArrayList<String>();
				StringTokenizer line = new StringTokenizer(s);
				String word=line.nextToken();

				if(lex_list.containsKey(word))
				{
					lex_list.remove(word);

				}


				while(line.hasMoreTokens())
				{
					properties.add(line.nextToken());

				}
				lex_list.put(word, properties);

				System.out.println("anjAm shod ! "+"\n");

				System.out.println("list lexicon ha :  "+"\n");


				lex_viewer();

				lex_menu();


				break;
			}


			default :
			{
				System.out.println("khaTA !!!");
				lex_menu();
			}

		}






	}



	public static void lex_delete()
	{


		try{

			System.out.println("ghanoon mored nazar khod ra vared konid");
			Scanner scan =new Scanner(System.in);

			String s=scan.nextLine();

			ArrayList<String> propeties = new ArrayList<String>();
			StringTokenizer line = new StringTokenizer(s);
			if(line.hasMoreTokens())
			{

				String word=line.nextToken();
				System.out.println("word   " +word);
				if(!lex_list.containsKey(word))
				{
					System.out.println("KhatA !!!...chenin ghAnooni vojood nadarad ");
					lex_menu();

				}
				else
				{
					lex_list.remove(word);

					File fout = new File("lex.txt");
					FileOutputStream fos = new FileOutputStream(fout);

					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					int size=lex_list.size();

					Set set = lex_list.entrySet();

					Iterator i = set.iterator();
					while(i.hasNext())
					{

						Map.Entry me = (Map.Entry)i.next();
						// System.out.print(new StringBuilder((String)me.getKey()).reverse().toString() + " : ");

						bw.write(me.getKey() +" : "+ me.getValue());
						bw.newLine();
					}



					bw.close();



					System.out.println("anjAm shod ! ");
					lex_menu();
				}
			}
			else
			{
				System.out.println("ghAnooni vared nashod ....khata");
				lex_menu();
			}
		}
		catch(IOException ex)
		{
			System.out.println("Error");
		}

	}

	public static void lex_viewer()
	{

		try
		{
			File fout = new File("lex.txt");
			FileOutputStream fos = new FileOutputStream(fout);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			int size=lex_list.size();

			Set set = lex_list.entrySet();

			Iterator i = set.iterator();
			while(i.hasNext())
			{

				Map.Entry me = (Map.Entry)i.next();
				System.out.print(new StringBuilder((String)me.getKey()).reverse().toString() + " : ");
				System.out.println( me.getValue());
				bw.write(me.getKey() +" : "+ me.getValue());
				bw.newLine();
			}



			bw.close();

			lex_menu();


		}

		catch(IOException ex)
		{
			System.out.println("Error");
		}

	}




	public static void grm_menu()
	{
		//dar inja karbar amal morede nazar khod 
		//ra baraye kar ba grammar entekhab mikonad
		Scanner scan = new Scanner(System.in);

		System.out.println("\n"+"\n"+"************************* GRAMMAR MENU *************************"+"\n"+"\n");

		System.out.println("adad amal mored nazar khod ra vared konid ");
		System.out.println("1 : ijad majmooei az grammar ha ");
		System.out.println("2 : viraayesh ya hazf majmooeye grammar ha  ");
		System.out.println("3: moshAhedeye majmooeye grammar ha  ");
		System.out.println("0 : bazgasht be menu ghabl  ");

		int answer=scan.nextInt();


		switch(answer)
		{
			case 1 :
			{
				grammar_reader();
				break;
			}
			case 2 :
			{
				grammar_editor();
				break;
			}

			case 3:
			{
				grammar_writer();
				break;
			}
			case 0 :
			{
				main_menu();
				break;
			}
			default :
			{
				System.out.println("KhatA !!!");
				main_menu();

			}

		}

	}



	public static void grammar_editor()
	{
		Scanner scan=new Scanner(System.in);
		System.out.println("baraye har gooneh taghir dar dastoorat grammar (hazf ya virAyesh), be file grammar rafte va dastoorat jadid ra emal konid ....pas az bastan file end ra vared konid ");
		String ans=scan.next();
		while(!ans.equals("end"))
		{
			ans=scan.next();
		}
		grammar_reader();

	}

	public static void grammar_writer()
	{

		ArrayList<Node> kids = new ArrayList<Node>();
		System.out.println(root.rule);





		for(int i=0; i<root.children.size(); i++)
		{
			for(int j=0; j<root.children.get(i).size(); j++)
			{

				kids.add(root.children.get(i).get(j));
			}

		}

		for(int a=0; a<kids.size(); a++)
		{

			dfs(kids.get(a));
		}


	}
	public static void dfs(Node pedar)
	{

		System.out.println(pedar.rule);
		ArrayList<Node> kids = new ArrayList<Node>();





		for(int i=0; i<pedar.children.size(); i++)
		{
			for(int j=0; j<pedar.children.get(i).size(); j++)
			{

				kids.add(pedar.children.get(i).get(j));
			}

		}
		if((kids.size())!=0)
		{

			for(int a=0; a<kids.size(); a++)
			{

				dfs(kids.get(a));
			}
		}
		return;


	}








	public static void grammar_reader()

	{
		Scanner scan = new Scanner(System.in);
		try{
			FileReader grreader = new FileReader("grammar.txt");
			BufferedReader br = new BufferedReader(grreader);

			String s = br.readLine();
			StringTokenizer step1 = new StringTokenizer(s,"|.->");
			String key = step1.nextToken();
			root = new Node(key,s, new ArrayList<List<Node>>());
			int i=root.size;

			while(step1.hasMoreTokens())
			{

				String childgroup = step1.nextToken();

				StringTokenizer step2 = new StringTokenizer(childgroup);
				while(step2.hasMoreTokens())
				{
					String child = step2.nextToken();

					root.children.add(new ArrayList<Node>());
					root.children.get(i).add(new Node(child,"", new ArrayList<List<Node>>()));


				}

				i++;
			}
			root.size=i;


			while((s = br.readLine()) != null)
			{

				step1 = new StringTokenizer(s,"|.->");
				if(step1.hasMoreTokens())
				{
					key = step1.nextToken();
					boolean value;

					value=root.finder(key,s);

					if(value)
					{
						System.out.println("grammar sahih ast"+"\n");
						grm_menu();
					}
					else

					{
						System.out.println("grammar sahih nist");
						grm_menu();

					}

				}
			}
			br.close();
			grreader.close();
		}
		catch(IOException ex)
		{
			System.out.println("Error reading file '"+ "grammar.txt" + "'");
		}



	}




	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public static void getTokens()
	{
		for(int i = 10; i < 19; ambiguous[i] = true, i++);
		for(int i = 0; i < 10; stickBefore[i] = true, i++);

		String fileName = "in.txt";
		String line = null;

		// read from lexicon
		try
		{
			FileReader fileReader = new FileReader("Lexicon.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null)
			{
				for (String tok : line.split("[A-Z]|[\t]"))
					lexiconWords.add(tok.trim());
			}

		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Unable to open file '"+fileName+"'");
		}
		catch(IOException ex)
		{
			System.out.println("Error reading file '"+ fileName + "'");
		}
		//


		try
		{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null)
			{
				for(String tok: line.split("[{[( )]},?!;\"]+"))
				{
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(tok);
					tokens.add(tmp);
				}

				// check from lexicon
				for(String lexWord: lexiconWords)
				{
					String[] lexTokens = lexWord.split(" ");
					int n = lexTokens.length;

					for(int i = 0; i+n <= tokens.size(); i++)
					{
						String word = "";
						for(int j = i; j < i+n; j++)
							word += tokens.get(j).get(0)+" ";
						word = word.trim();


						String tmp = tokens.get(i).get(0);
						if(word.equals(lexWord))
							for(int j = i+1; j < i+n; j++)
							{
								tokens.get(j).set(0, tmp+" "+tokens.get(j).get(0));
								tmp = tokens.get(j).get(0);
								tokens.remove(j-1);
								j--;
								n--;
							}
					}

				}

				// Check two consecutive tokens
				for(int i = 0; i < tokens.size(); i++)
					if(!lexiconWords.contains(tokens.get(i).get(0)))
					{
						String token_i = tokens.get(i).get(0);

						if (IsMorpheme(token_i) && !ambiguous[getMorphemeIdx(token_i)])
						{
							if(stickBefore[getMorphemeIdx(token_i)])
							{
								if(i+1 >= tokens.size())	continue;

								String jointString = token_i +"~"+ tokens.get(i+1).get(0);
								tokens.get(i+1).set(0, jointString);
								tokens.remove(i);

							}
							else
							{
								if(i-1 < 0)	continue;

								String jointString = tokens.get(i-1).get(0) +"~"+ token_i;
								tokens.get(i).set(0, jointString);
								tokens.remove(i-1);
								i--;
							}
						}
						if (IsMorpheme(token_i) && ambiguous[getMorphemeIdx(token_i)])
						{
							if(stickBefore[getMorphemeIdx(token_i)])
							{
								if(i+1 >= tokens.size())	continue;

								tokens.get(i+1).add(token_i + "~" + tokens.get(i + 1).get(0));
							}
							else
							{
								if(i-1 < 0)	continue;

								tokens.get(i).add(tokens.get(i - 1).get(0) + "~" + token_i);
							}
						}
					}


				for(int i = 0; i < tokens.size(); i++)
					for(int k = 0; k < tokens.get(i).size(); k++)
					{
						String token_i = tokens.get(i).get(k);

						// unknown words
						if (!lexiconWords.contains(token_i))
						{
							for (int j = 0; j < 7; j++)
								if (token_i.contains(""+nonFinalForm[j]))
								{
									String newWord = (new StringBuffer(token_i).insert(token_i.indexOf(nonFinalForm[j])+1, " ")).toString();
									if(lexiconWords.contains(newWord))
										tokens.get(i).add(newWord);
								}
						}

						//tokenization rules for Acronyms, Abbreviation, and sentence boundries

						if(IsAcronym(token_i)[1] == 0 && IsAcronym(token_i)[0] == 1 ||
								IsAbbreviation(token_i)[1] == 0 && (IsAbbreviation(token_i)[0] == 1 || IsAbbreviation(token_i)[0] == 4))
						{
							// there is no action to be taken
						}

						if(IsAcronym(token_i)[0] == 1 && IsAcronym(token_i)[1] == 2 ||
								IsAbbreviation(token_i)[1] == 2 && (IsAbbreviation(token_i)[0] == 1 || IsAbbreviation(token_i)[0] == 2 || IsAbbreviation(token_i)[0] == 4))
						{
							//create sentence boundary
							if(token_i.contains("."))
								for(String tok: token_i.split("[.]+"))
									tokens.get(i).add(tok);
						}

						if(IsAcronym(token_i)[0] == 2 && IsAcronym(token_i)[1] == 1)
							for(String tok: token_i.split("[.~]+"))                  // create word token
								if(dictionary.contains(tok))
									tokens.get(i).add(tok);

						if(IsAbbreviation(token_i)[0] == 3)
						{
							for(String tok: token_i.split("[.~]+"))            	// create word token
								if(dictionary.contains(tok))
									tokens.get(i).add(tok);

							//create sentence boundary
							if(token_i.contains("."))
								for(String tok: token_i.split("[.]+"))
									tokens.get(i).add(tok);
						}
					}


				// Check two consecutive tokens again
				for(int i = 0; i < tokens.size(); i++)
					for(int k = 0; k < tokens.get(i).size(); k++)
						if(!lexiconWords.contains(tokens.get(i).get(k)))
						{
							String token_i = tokens.get(i).get(k);

							// check in lexicon
							if(i+1 < tokens.size())
								for(int j = 0; j < tokens.get(i+1).size(); j++)
								{
									String jointString = token_i + "~" + tokens.get(i + 1).get(j);
									if(!lexiconWords.contains(jointString))	continue;

									tokens.get(i).set(k, jointString);
									tokens.get(i+1).remove(j);
									j--;
									if(tokens.get(i+1).isEmpty())
									{
										tokens.remove(i+1);
										i--;
										break;
									}
								}


							if (IsMorpheme(token_i) && !ambiguous[getMorphemeIdx(token_i)])
							{
								if(stickBefore[getMorphemeIdx(token_i)])
								{
									if(i+1 >= tokens.size())	continue;

									for(int j = 0; j < tokens.get(i+1).size(); j++)
									{
										String jointString = token_i + "~" + tokens.get(i + 1).get(j);
										tokens.get(i).set(k, jointString);
										tokens.get(i+1).remove(j);
										j--;
										if(tokens.get(i+1).isEmpty())
										{
											tokens.remove(i+1);
											i--;
											break;
										}
									}
								}
								else
								{
									if(i-1 < 0)	continue;

									for(int j = 0; j < tokens.get(i-1).size(); j++)
									{
										String jointString = tokens.get(i - 1).get(j) + "~" + token_i;
										tokens.get(i).set(k, jointString);
										tokens.get(i - 1).remove(j);
										j--;
										if(tokens.get(i-1).isEmpty())
										{
											tokens.remove(i-1);
											i--;
											break;
										}
									}
								}
							}
							else if (IsMorpheme(token_i) && ambiguous[getMorphemeIdx(token_i)])
							{
								if(stickBefore[getMorphemeIdx(token_i)])
								{
									if(i+1 >= tokens.size())	continue;

									for(int j = 0; j < tokens.get(i+1).size(); j++)
										tokens.get(i+1).add(token_i+"~"+tokens.get(i+1).get(j));
								}
								else
								{
									if(i-1 < 0)	continue;

									for(int j = 0; j < tokens.get(i-1).size(); j++)
										tokens.get(i).add(tokens.get(i-1).get(j)+"~"+token_i);
								}
							}
						}




			}

			bufferedReader.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Unable to open file '"+fileName+"'");
		}
		catch(IOException ex)
		{
			System.out.println("Error reading file '"+ fileName + "'");
		}



		Write();

	}

	// tokenizer needs to produce 2 kinds of tokens in ambiguous words
	private static int[] IsAbbreviation(String word)     // returns a pair: (format num, ambiguity (1:ambiguous with word , 2:ambiguous with EOS) )
	{
		//in cases 1,2, and 4 tokenizer will produce 2 kinds of tokens: the token is abbrevi, abbrevi with end of sentence

		int[] res = new int[2];
		int n = word.length();

		if(n>=2 && isFinalForm(word.charAt(0)) && word.charAt(1) == '~')
		{
			res[0] = 1;
			if(word.charAt(word.length()-1) == '.')	res[1] = 2;
		}
		else if(isFarsiAlphabetic(word.charAt(0)) && word.charAt(word.length()-1) == '.' && isFinalForm(word.charAt(word.indexOf(".")-1)))
		{
			res[0] = res[1] = 2;
		}
		else if(isFarsiAlphabetic(word.charAt(0)) && word.contains(".") && !isFinalForm(word.charAt(word.indexOf(".")-1)))
		{
			// the token have 3 possible ways to be tokenized: it is an abbreviation, is an abbreviation making the EOS, or is a word with a sentence boundary
			res[0] = 3;
		}

		boolean ok = true;
		String[] tokens = word.split("[ .~]");
		for(String str: tokens)
			if(str.length() != 1 || !isFinalForm(str.charAt(0)))
				ok = false;
		if(ok)
		{
			res[0] = 4;
			if(word.charAt(word.length()-1) == '.')	res[1] = 2;
		}

		return res;
	}

	private static int[] IsAcronym(String word)  // returns a pair: (format num, ambiguity (1:ambiguous with word , 2:ambiguous with EOS) )
	{
		int[] res = new int[2];
		int n = word.length();

		if(word.contains(".") && (word.indexOf(".")-1>=0 && isFarsiAlphabetic(word.charAt(word.indexOf(".")-1)) || word.indexOf(".")-2>=0 && word.charAt(word.indexOf(".")-1) == '~'  &&
				isFarsiAlphabetic(word.charAt(word.indexOf(".")-2))) && word.indexOf(".")+1<n && isFarsiAlphabetic(word.charAt(word.indexOf(".") + 1)))
		{
			res[0] = 1;
			if(word.charAt(word.length()-1) == '.')
				res[1] = 2;
		}

		else if(word.contains("~") && word.indexOf("~")-1>=0 && isFinalForm(word.charAt(word.indexOf("~")-1)) && word.indexOf("~")+1<n && isFarsiAlphabetic(word.charAt(word.indexOf("~")+1)) && endsWithFinalForm(word.substring(word.indexOf("~")+1)))
		{
			res[0] = 2;
			res[1] = 1;
		}

		return res;
	}

	private static boolean endsWithFinalForm(String str)
	{
		for(int j = 0; j < 7; j++)
			if (str.endsWith(""+nonFinalForm[j]))
				return false;
		return true;
	}

	private static boolean isFinalForm(char ch)
	{
		for(char nff: nonFinalForm)
			if(ch == nff)
				return false;
		return true;

	}

	private static boolean isFarsiAlphabetic(char ch)
	{
		for(char alph: farsiAlphabet)
			if(alph == ch)
				return true;
		return false;
	}

	private static int getMorphemeIdx(String token)
	{
		for(int i = 0; i < morphemes.length; i++)
			if(morphemes[i].equals(token))
				return i;
		return -1;
	}

	private static boolean IsMorpheme(String token)
	{
		for(String str: morphemes)
			if(str.equals(token))
				return true;
		return false;
	}

	private static void Write()
	{
		String fileName = "out.txt";

		try {
			FileWriter fileWriter = new FileWriter(fileName);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("تمام حالات توکن ها:\n");
			for(int i = 0; i < tokens.size(); i++)
			{
				bufferedWriter.write("-----\n");
				for(String token: tokens.get(i))
					bufferedWriter.write(token+"\n");

			}
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");

		}

	}

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	public static void main(String[]args)

	{
		Scanner scan = new Scanner(System.in);
		main_menu();

	}
}
	
	
	
	
	
	

	
	
	
	
	
	

