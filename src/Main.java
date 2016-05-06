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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class Main {
public static int FileCount=0;
public static int DirCount=0;
public static int dcl00j=0;
public static int dcl01j=0;
public static int msc01j=0;
	public static void main(String[] args) throws FileNotFoundException  {
		FileOutputStream FileLoc = new FileOutputStream("output/errorlog.txt",true); // Save output to this file
		   
	    PrintStream LogFile = new PrintStream(FileLoc);
	    // Specify input folder
		//File currentDir = new File("/media/rahul/Education/Project/src/M/Sources/packages/inputmethods/LatinIME/java/src/com/android/inputmethod/latin/");
		 //File currentDir = new File("/home/rahul/workspace/rulechecker/Samples"); // current directory
		File currentDir = new File("/media/rahul/Education/Project/src/M/Sources/"); // current directory
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		// Print output 
		System.out.println("*****************************************************Automation Started********************************************");
		Date date = new Date();
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
		System.out.println("**********************Report***********************");
		System.out.println("---------------------------------------------------");
		System.out.println(" Rule        Total Violation");
		System.out.println("---------------------------------------------------");
		System.out.println("DCL00J   -       "+dcl00j);
		System.out.println("DCL01J   -       "+dcl01j);
		System.out.println("MSC01J   -       "+msc01j);
		System.out.println("---------------------------------------------------");
		System.out.println("Total "+DirCount+" directories searched and "+FileCount+" files scanned ");
		System.out.println("");
		System.out.println("");
		LogFile.println("");
		LogFile.println("");
		LogFile.println("**********************Report***********************");
		LogFile.println("---------------------------------------------------");
		LogFile.println(" Rule        Total Violation");
		LogFile.println("---------------------------------------------------");
		LogFile.println("DCL00J   -      "+dcl00j);
		LogFile.println("DCL01J   -      "+dcl01j);
		LogFile.println("MSC01J   -       "+msc01j);
		LogFile.println("---------------------------------------------------");
		LogFile.println("Total "+DirCount+" directories searched and "+FileCount+" files scanned ");
		LogFile.println("");LogFile.println("");
		Date date1 = new Date();
		System.out.println("*****************************************************Automation Finished********************************************");
		System.out.println(dateFormat.format(date1));
		System.out.println("**********************************************************************************************************");
		LogFile.println("*****************************************************Automation Finished***********************************************");
		LogFile.println(dateFormat.format(date1));
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
	int pos=0;
	ArrayList<Integer> LineList = new ArrayList<Integer>();
	ArrayList<String> VarList = new ArrayList<String>();
	FileOutputStream FileLoc = new FileOutputStream("output/errorlog.txt",true);
	  
    PrintStream LogFile = new PrintStream(FileLoc);
    
	public MyVisitor(CompilationUnit cu, String source) throws IOException {
		super();
		this.cu = cu;
		this.source = source;
	}
	//***********************It is for getting class name********
	public boolean visit(TypeDeclaration node) {
	Clno=0;
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
	  public boolean visit(MethodDeclaration md) {

	        if (md.getName().toString().equals(Classname)) {
	            md.accept(new ASTVisitor() {
	                public boolean visit(Assignment fd) {
	               String s=fd.getRightHandSide().toString();
	                	
	                    //System.out.println("in method: " + fd.getRightHandSide());
	                    for(int i=0;i<VarList.size();i++)
	                    if(s.contains(VarList.get(i)))
	                    {
	                    	 
	                    	 if(LineList.get(i)>0&&Clno>0){ // Check variable declaration and constructor initialization with "Static" keyword is present
	    					if(LineList.get(i)>Clno)//  Check variable declaration is after constructor initialization 
	    					{
	    						System.out.println("DCL00-J. Prevent class initialization cycles (Intraclass Cycle) problem. 'static' constructor initialization at line " +Clno+", invoked at line  " +cu.getLineNumber(fd.getStartPosition())+ " and variable ' "+VarList.get(i)+" ' initialization at line "+LineList.get(i)+". Please do 'static' constructor initialization after all 'static' variable initialization");								    
	    						LogFile.println("DCL00-J. Prevent class initialization cycles (Intraclass Cycle) problem. 'static' constructor initialization at line " +Clno+", invoked at line  " +cu.getLineNumber(fd.getStartPosition())+ " and variable ' "+VarList.get(i)+" ' initialization at line "+LineList.get(i)+". Please do 'static' constructor initialization after all 'static' variable initialization");								    
	    						//System.out.println(Lastlno+"  "+Clno);
	    						// LogFile.println(Lastlno+"  "+Clno);
	    						Main.dcl00j++;
	    					}
	    				} 
	                    }
	                    return true;
	                }
	            });
	          
	            
	           // System.out.println(VarList);
	           // System.out.println(LineList);
	        }
	        return true;
	    }
 	public boolean visit(FieldDeclaration node) {
		 Type t=node.getType();
 if(t.toString().equals(this.Classname)&&node.modifiers().toString().contains("static"))//check constructor initialization with "Static" keyword
			{
			 
			Clno=cu.getLineNumber(node.getStartPosition());
			//System.out.println(Classname+Clno);
			}
		 else if (node.modifiers().toString().contains("static")) {
			// System.out.println(Classname+Clno);
	            node.accept(new ASTVisitor() {	
		 public boolean visit(VariableDeclarationFragment fd) {
         	 LineList.add(cu.getLineNumber(fd.getStartPosition()));
         	 VarList.add(fd.getName().toString());
          //   System.out.println("in frag: " + fd.getName());
             return true;
         }
     });
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
				Main.msc01j++;
				/*Here some code needed to check this FieldDeclaration node (Constructor invoked) is the right most
				FieldDeclaration node if yes no code violation */
			 }
			 
			return true; // do not continue to avoid usage info
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
			    Main.dcl01j++;
				}
		//*********************************************************************************************************
		//*********************************************************************************************************
	
	}
	 
	}
