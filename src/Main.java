import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class Main {
public static int FileCount=0;
public static int DirCount=0;
	public static void main(String[] args) throws FileNotFoundException  {
		FileOutputStream FileLoc = new FileOutputStream("output/errorlog.txt",true); // Save output to this file
		   
	    PrintStream LogFile = new PrintStream(FileLoc);
		//File currentDir = new File("/home/rahul/workspace1/javaparser-master/");
		File currentDir = new File("/home/rahul/workspace/rulechecker/input"); // current directory
		//File currentDir = new File("/media/rahul/Education/Project/src/M/Sources/"); // current directory
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		// Print output 
		System.out.println("*****************************************************Automation Started********************************************");
		System.out.println(dateFormat.format(date));
		System.out.println("**********************************************************************************************************");
		LogFile.println("*****************************************************Automation Started***********************************************");
		LogFile.println(dateFormat.format(date));
		LogFile.println("*************************************************************************************************************");
		System.out.println("");
		System.out.println("");
		LogFile.println("");
		LogFile.println("");
	 	displayDirectoryContents(currentDir);
		System.out.println("");
		System.out.println("");
		System.out.println("Total "+DirCount+" directories searched and "+FileCount+" files scanned ");
		LogFile.println("");
		LogFile.println("");
		LogFile.println("Total "+DirCount+" directories searched and "+FileCount+" files scanned ");
		System.out.println("*****************************************************Automation Finished********************************************");
		System.out.println(dateFormat.format(date));
		System.out.println("**********************************************************************************************************");
		LogFile.println("*****************************************************Automation Finished***********************************************");
		LogFile.println(dateFormat.format(date));
		LogFile.println("*************************************************************************************************************");
	
		
	}
// Read and parse all java file in a directory and sub directory
	public static void displayDirectoryContents(File dir) throws FileNotFoundException {
		String str;
		FileOutputStream FileLoc = new FileOutputStream("output/errorlog.txt",true);// Save output to this file
		   PrintStream LogFile = new PrintStream(FileLoc);
		     
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println("directory:" + file.getCanonicalPath());
					displayDirectoryContents(file);
					DirCount++;
				} else {
					 if(file.getCanonicalPath().toString().endsWith(".java"))
	                  {
	                     System.out.println("     file:" + file.getCanonicalPath());
	                     LogFile.println("     file:" + file.getCanonicalPath());
	                     FileCount++;
	                     BufferedReader br = new BufferedReader(new FileReader(file.getCanonicalPath().toString()));
	     				try {
	     				    StringBuilder sb = new StringBuilder();
	     				    String line = br.readLine();
	     				    while (line != null) {
	     				        sb.append(line);
	     				        sb.append(System.lineSeparator());
	     				        line = br.readLine();
	     				    }
	     				   str = sb.toString();
	     				} finally {
	     				    br.close();
	     				}
	                      
	                     ASTParser parser = ASTParser.newParser(AST.JLS3);
	     			    parser.setSource(str.toCharArray());
	     				parser.setKind(ASTParser.K_COMPILATION_UNIT);
	     			    CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	     		//*******************************************************************************
	      
	     			    // System.out.println(str.toCharArray());
	     		//TypeFinderVisitor v = new TypeFinderVisitor();
	     		cu.accept(new MyVisitor(cu,str));	
	                  }
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
class MyVisitor extends ASTVisitor{
	CompilationUnit cu;
	String source;
	String Classname;
	int LineNo;
	int Clno=0;
	int Lastlno=0;
	FileOutputStream FileLoc = new FileOutputStream("output/errorlog.txt",true);
	   
    PrintStream LogFile = new PrintStream(FileLoc);
/*	File file = new File("output/errorlog.txt");
	FileWriter fw = new FileWriter(file);
	PrintWriter pw = new PrintWriter(fw);
	pw.println("Hello World");
	pw.close();*/
	public MyVisitor(CompilationUnit cu, String source) throws IOException {
		super();
		this.cu = cu;
		this.source = source;
		// System.out.println("here");
		/* List types = cu.types();    
		 TypeDeclaration typeDec = (TypeDeclaration) types.get(1); 
		 //typeDec value become class
		 this.Classname=typeDec.getName().toString();*/
			//System.out.println("className:" + typeDec.getName());
			
			
			 
	}
	//***********************It is for getting class name********
	public boolean visit(TypeDeclaration node) {
	
	// It is useful if multiple classes are present in a single .java file			
		SimpleName name=node.getName(); // Gives class name of current visiting node
		this.Classname=name.toString(); // Saving to a global variable for many other function
		this.LineNo=cu.getLineNumber(name.getStartPosition());// Line number saving to a global variable. It is useful for giving error message
		try {
			ruleDcl01(); // Calling the method for DCL01-J. Currently added as a method. Later it may include this block.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	//*************************Block Ends Here*************************************
	//***********************Prevent class initialization cycles(IntraClass)*********
	
 	public boolean visit(FieldDeclaration node) {
		 Type t=node.getType();
//********************************* Constructor invocation node position checking  ********************	
// ******** To implement DCL001-J IntraClass allow constructor after all variable declaration**********
		 if(t.toString().equals(this.Classname)&&node.modifiers().toString().contains("static"))//check constructor initialization with "Static" keyword
			{
				  
				//System.out.println("Class object creation found at line "+node.modifiers());
			 this.Clno=cu.getLineNumber(node.getStartPosition()); //save the constructor initialization line number
				 
			}
		 else if(node.modifiers().toString().contains("static"))//check variable declaration with "Static" keyword
				 {
			 this.Lastlno=cu.getLineNumber(node.getStartPosition());//save the variable declaration line number
				 }
			 if(Lastlno>0&&Clno>0){ // Check variable declaration and constructor initialization with "Static" keyword is present
					if(Lastlno>Clno)//  Check variable declaration is after constructor initialization 
					{
						System.out.println("DCL00-J. Prevent class initialization cycles (Intraclass Cycle) problem. 'static' constructor initialization at line " +Clno+", and variable initialization at line "+Lastlno+". Please do 'static' constructor initialization after all 'static' variable initialization");								    
						LogFile.println("DCL00-J. Prevent class initialization cycles (Intraclass Cycle) problem. 'static' constructor initialization at line " +Clno+", and variable initialization at line "+Lastlno+". Please do 'static' constructor initialization after all 'static' variable initialization");								    
						//System.out.println(Lastlno+"  "+Clno);
						// LogFile.println(Lastlno+"  "+Clno);
					}
				}
			 
			return true; //  
		} 
	 
	
 
	public boolean visit(WhileStatement stmt) {
		
		String cntn= stmt.getExpression().toString();// Get condition True or False
		String sn=stmt.getBody().toString(); // Get body 
		sn=sn.replace("{",""); 
		sn=sn.replace("}","");
		sn=sn.replaceAll("\\s+","");
		//Removed all { } and whitespace may be a programmer will type "while(true){}" so body becomes {}
//********************************* Constructor invocation node position checking  ********************	
//******** To implement DCL001-J IntraClass allow constructor after all variable declaration**********
		//System.out.println(sn);
		if(cntn.equals("true")&&sn.equals(""))//Checking condition is true and body is empty
			 {
				 
				System.out.println("MSC01-J. Do not use an empty infinite loop. Error at line "+cu.getLineNumber(stmt.getStartPosition()));
				LogFile.println("MSC01-J. Do not use an empty infinite loop. Error at line "+cu.getLineNumber(stmt.getStartPosition()));
				
				/*Here some code needed to check this FieldDeclaration node (Constructor invoked) is the right most
				FieldDeclaration node if yes no code violation */
			 }
			 
			return false; // do not continue to avoid usage info
		}
	public void ruleDcl01() throws IOException{
		//*********************Read Class List*******************************************
				Path filePath = new File("javaclass").toPath();
				   Charset charset = Charset.defaultCharset();        
				   List<String> stringList = Files.readAllLines(filePath, charset);
				   String[] stringArray = stringList.toArray(new String[]{});
				   int retVal =0;
		//*******************************************************************************
		//***********************Do not reuse public identifiers from the Java Standard Library(Detection)*********
		//********************************* Comparing Class name with Java Standard Class names********************		
				String searchVal = this.Classname; // typeDec.getName() gives the class name
			    retVal=Arrays.binarySearch(stringArray,searchVal); // binary search using java standard method. If found it will return the index(index>=0)
				if(retVal>=0)//If index greater than or equals zero match found. ie class name is same as a java standard class name. So a warning message for programmer.
				{
				System.out.println("DCL01-J. Do not reuse public identifiers from the Java Standard Library:  Error at line "+LineNo+" "+". '"+searchVal+"' is a built-in Java Class name. Please use different one ");
			    LogFile.println("DCL01-J. Do not reuse public identifiers from the Java Standard Library:  Error at line "+LineNo+" "+". '"+searchVal+"' is a built-in Java Class name. Please use different one ");
			    
				}
		//*********************************************************************************************************
		//*********************************************************************************************************
	
	}
	
	}
