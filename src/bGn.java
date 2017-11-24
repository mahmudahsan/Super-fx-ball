class bGn{
	String name, id;
	long number;
	int arr[] = {57,3,2,7,22,19,11,13,23,29,37,31,67,43,47,41,43,59,63,39,69, 73, 71, 89, 23, 87,79, 93, 91, 107, 103 };
	
	bGn(String name, String id){
		this.name = name.toLowerCase().trim();
		this.id = id;
		gen();
	}
	
	private void gen(){
		if (name == null || id == null)
			return;
		
		StringBuffer br = new StringBuffer(name);
		br.append(id);
	
		int ing;
		number = 1;
		for (int i = 0; i < br.length(); ++i){
			ing = (arr[i%30] * (int) br.charAt(i) ) % Integer.MAX_VALUE;
			
			number = (number + 235791 * ing+13) % Integer.MAX_VALUE;
			
		}	
		if (number < Integer.MAX_VALUE / 2){
			number += Integer.MAX_VALUE / 3;
			number = number % Integer.MAX_VALUE;
		}
		
		if (number < 0)
			number = Integer.MAX_VALUE-99;
		
		
	}
	public String decToOther(){
		String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer finals = new StringBuffer();
		int n, temp;
	
		do{
			temp = (int) (number % 18);
			finals.append(str.charAt(temp));
			
			number = number / 18;
		}while (number != 0);
	
		return finals.toString();
	}
	
}

