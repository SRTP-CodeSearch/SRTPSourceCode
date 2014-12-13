import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import NodeHandle.*;

public class Requestor extends FileASTRequestor {
	public String projName;
	public ProjectNode projNode;
	public Requestor(String projName , String fileLocation) {
			// TODO Auto-generated constructor stub
		this.projName=projName;
		projNode = new ProjectNode();
		if(projNode.checkNodeExist(projName, fileLocation)){
		}
		else{
			projNode.createNode(projName, fileLocation);
		}
		}

	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit ast) {		
		
		//visit the ast tree
			ast.accept(new ASTVisitor() {
				ClassNode classNode = new ClassNode();
				ClassNode invokedClass=new ClassNode();
				MethodNode methodNode = new MethodNode();
				MethodNode invokedMethod = new MethodNode();

				//show VariableDeclaration
//				public boolean visit(VariableDeclarationFragment node){
					//Variable type is a array
//					if(node.resolveBinding().getType().isArray()){
//						System.out.println("This is a Array");
//						System.out.println(node.resolveBinding().getType().getElementType().getQualifiedName());
//						System.out.println(node.resolveBinding().getVariableDeclaration().getName());
//					}
//					//Variable type is generic
//					else if(node.resolveBinding().getType().isParameterizedType()){
//						System.out.println("This is a Generic Type");
//						for(ITypeBinding iter : node.resolveBinding().getType().getTypeArguments()){
//							System.out.println(iter.getQualifiedName());
//						}
//						System.out.println(node.resolveBinding().getVariableDeclaration().getName());						
////						System.out.println(node.resolveBinding().getType().getTypeArguments()[0].getQualifiedName());
//					}
//					//Variable type is interface
//					else if(node.resolveBinding().getType().isInterface()){
//						System.out.println("This is a interface");
//						System.out.println(node.resolveBinding().getType().getQualifiedName());
//						System.out.println(node.resolveBinding().getVariableDeclaration().getName());						
//					}else{
//						System.out.println(node.resolveBinding().getType().getQualifiedName());
//						System.out.println(node.resolveBinding().getVariableDeclaration().getName());
//					}
//					
//					System.out.println("\n");
//					return true;
//				}
//				
//				public boolean visit(PackageDeclaration node){
//					packagename=node.getName();
//					return true;					
//				}
				
				//Parse the enum type
				public boolean visit(EnumDeclaration node){
					String nodeName = node.getName().getFullyQualifiedName();
					String nodePackageName = node.resolveBinding().getPackage().getName();
					if(classNode.thisNode==null){
						if(classNode.checkNodeExist(nodeName, nodePackageName, "enum")){
							if(!node.superInterfaceTypes().isEmpty()){
								ClassNode implementNode = new ClassNode();
								for(ITypeBinding iter1 : node.resolveBinding().getInterfaces()){
									String implemInterfaceName = iter1.getName();
									String implemPackagename = node.resolveBinding().getPackage().getName();
									if(implementNode.checkNodeExist(implemInterfaceName, implemPackagename, "interface")){
										implementNode.setImplementRelToClass(classNode);
									}else{
										implementNode.createNode(implemInterfaceName, implemPackagename, "interface");
										implementNode.setImplementRelToClass(classNode);
									}
								}
							}
						}else{
							classNode.createNode(nodeName, nodePackageName, "enum");
							if(!node.superInterfaceTypes().isEmpty()){
								ClassNode implementNode = new ClassNode();
								for(ITypeBinding iter1 : node.resolveBinding().getInterfaces()){
									String implemInterfaceName = iter1.getName();
									String implemPackagename = node.resolveBinding().getPackage().getName();
									if(implementNode.checkNodeExist(implemInterfaceName, implemPackagename, "interface")){
										implementNode.setImplementRelToClass(classNode);
									}else{
										implementNode.createNode(implemInterfaceName, implemPackagename, "interface");
										implementNode.setImplementRelToClass(classNode);
									}
								}
							}
						}
						classNode.setContainRelToClass(projNode);
					}
					System.out.println(nodeName);
					System.out.println(node.resolveBinding().getPackage().getName());
					return true;
					
				}
				
				//Show the class and interface name
				public boolean visit(TypeDeclaration node){
					String nodeName = node.getName().getFullyQualifiedName();
					String nodePackageName = node.resolveBinding().getPackage().getName();
					//node is an interface
					if(classNode.thisNode==null){
					if(node.isInterface()==true){
						System.out.println("Interface:"+node.getName());						
						ClassNode extendNode = new ClassNode();						
						if(classNode.checkNodeExist(nodeName, nodePackageName, "interface")){
							if(node.getSuperclassType()!=null){					
								String extendClassName = node.getSuperclassType().resolveBinding().getName();
								String extendPackageName = node .getSuperclassType().resolveBinding().getPackage().getName();
								if(extendNode.checkNodeExist(extendClassName, extendPackageName, "interface")){
									extendNode.setExtendRelToClass(classNode);
								}else{
									extendNode.createNode(extendClassName, extendPackageName, "interface");
									extendNode.setExtendRelToClass(classNode);
								}
							}
							classNode.setContainRelToClass(projNode);
						}else{							
							classNode.createNode(nodeName, nodePackageName, "interface");
							classNode.setContainRelToClass(projNode);
							if(node.getSuperclassType()!=null){					
								String extendClassName = node.getSuperclassType().resolveBinding().getName();
								String extendPackageName = node .getSuperclassType().resolveBinding().getPackage().getName();
								if(extendNode.checkNodeExist(extendClassName, extendPackageName, "interface")){
									extendNode.setExtendRelToClass(classNode);
								}else{
									extendNode.createNode(extendClassName, extendPackageName, "interface");
									extendNode.setExtendRelToClass(classNode);
								}
							}
						}
					}
					//node is an abstract class or a class
					else {
						// boolean flag = true;
						// for(Object iter : node.modifiers()){
						// 	//node is an abstract class
						// 	if(iter.toString().equals("abstract")){
						// 		System.out.println("Abstract_Class:"+nodeName);
						// 		if(classNode.checkNodeExist(nodeName, nodePackageName, "abstract")){
						// 			if(node.getSuperclassType()!=null){	
						// 				ClassNode extendNode = new ClassNode();
						// 				String extendClassName = node.getSuperclassType().resolveBinding().getName();
						// 				String extendPackageName = node .getSuperclassType().resolveBinding().getPackage().getName();
						// 				if(extendNode.checkNodeExist(extendClassName, extendPackageName, "class")){
						// 					extendNode.setExtendRelToClass(classNode);
						// 				}else{
						// 					extendNode.createNode(extendClassName, extendPackageName, "class");
						// 					extendNode.setExtendRelToClass(classNode);
						// 				}
						// 			}
						// 			if(node.superInterfaceTypes().isEmpty()){								
						// 			}else{
						// 				ClassNode implementNode = new ClassNode();
						// 				for(ITypeBinding iter1 : node.resolveBinding().getInterfaces()){
						// 					String implemInterfaceName = iter1.getName();
						// 					String implemPackagename = node.resolveBinding().getPackage().getName();
						// 					if(implementNode.checkNodeExist(implemInterfaceName, implemPackagename, "interface")){
						// 						implementNode.setImplementRelToClass(classNode);
						// 					}else{
						// 						implementNode.createNode(implemInterfaceName, implemPackagename, "interface");
						// 						implementNode.setImplementRelToClass(classNode);
						// 					}
						// 				}
						// 			}									
						// 		}else{
						// 			classNode.createNode(nodeName, nodePackageName, "abstract");
						// 			if(node.getSuperclassType()!=null){	
						// 				ClassNode extendNode = new ClassNode();
						// 				String extendClassName = node.getSuperclassType().resolveBinding().getName();
						// 				String extendPackageName = node .getSuperclassType().resolveBinding().getPackage().getName();
						// 				if(extendNode.checkNodeExist(extendClassName, extendPackageName, "interface")){
						// 					extendNode.setExtendRelToClass(classNode);
						// 				}else{
						// 					extendNode.createNode(extendClassName, extendPackageName, "interface");
						// 					extendNode.setExtendRelToClass(classNode);
						// 				}
						// 			}
						// 			if(node.superInterfaceTypes().isEmpty()){								
						// 			}else{
						// 				ClassNode implementNode = new ClassNode();
						// 				for(ITypeBinding iter1 : node.resolveBinding().getInterfaces()){
						// 					String implemInterfaceName = iter1.getName();
						// 					String implemPackagename = node.resolveBinding().getPackage().getName();
						// 					if(implementNode.checkNodeExist(implemInterfaceName, implemPackagename, "interface")){
						// 						implementNode.setImplementRelToClass(classNode);
						// 					}else{
						// 						implementNode.createNode(implemInterfaceName, implemPackagename, "interface");
						// 						implementNode.setImplementRelToClass(classNode);
						// 					}
						// 				}
						// 			}	
						// 		}
						// 	flag = false;
						// 	}
						// }
						
						//node is a class
//							System.out.println(node.getSuperclassType().resolveBinding().getPackage().getName());
							if(classNode.checkNodeExist(nodeName, nodePackageName, "class")){
								if(node.getSuperclassType()!=null){	
									ClassNode extendNode = new ClassNode();
									String extendClassName = node.getSuperclassType().resolveBinding().getName();
									String extendPackageName = node .getSuperclassType().resolveBinding().getPackage().getName();
									if(extendNode.checkNodeExist(extendClassName, extendPackageName, "class")){
										extendNode.setExtendRelToClass(classNode);
									}else{
										extendNode.createNode(extendClassName, extendPackageName, "class");
										extendNode.setExtendRelToClass(classNode);
									}
								}
								if(node.superInterfaceTypes().isEmpty()){								
								}else{
									ClassNode implementNode = new ClassNode();
									for(ITypeBinding iter1 : node.resolveBinding().getInterfaces()){
										String implemInterfaceName = iter1.getName();
										String implemPackagename = node.resolveBinding().getPackage().getName();
										if(implementNode.checkNodeExist(implemInterfaceName, implemPackagename, "interface")){
											implementNode.setImplementRelToClass(classNode);
										}else{
											implementNode.createNode(implemInterfaceName, implemPackagename, "interface");
											implementNode.setImplementRelToClass(classNode);
										}
									}
								}
								
								classNode.setContainRelToClass(projNode);
							}else{
								classNode.createNode(nodeName, nodePackageName, "class");
								classNode.setContainRelToClass(projNode);
								if(node.getSuperclassType()!=null){	
									ClassNode extendNode = new ClassNode();
									String extendClassName = node.getSuperclassType().resolveBinding().getName();
									String extendPackageName = node .getSuperclassType().resolveBinding().getPackage().getName();
									if(extendNode.checkNodeExist(extendClassName, extendPackageName, "class")){
										extendNode.setExtendRelToClass(classNode);
									}else{
										extendNode.createNode(extendClassName, extendPackageName, "class");
										extendNode.setExtendRelToClass(classNode);
									}
								}
								if(node.superInterfaceTypes().isEmpty()){								
								}else{
									ClassNode implementNode = new ClassNode();
									for(ITypeBinding iter1 : node.resolveBinding().getInterfaces()){
										String implemInterfaceName = iter1.getName();
										String implemPackagename = node.resolveBinding().getPackage().getName();
										if(implementNode.checkNodeExist(implemInterfaceName, implemPackagename, "interface")){
											implementNode.setImplementRelToClass(classNode);
										}else{
											implementNode.createNode(implemInterfaceName, implemPackagename, "interface");
											implementNode.setImplementRelToClass(classNode);
										}
									}
								}	
							}


						System.out.println("Class:"+node.getName());
//						if(packagename.equals("")){
//							System.out.println("Class_packagename:src");
//							classNode.createNode(node.getName().getFullyQualifiedName(), packagename, "class");
//						}else{
//							System.out.println("Class_packagename:"+packagename);	
//							classNode.createNode(node.getName().getFullyQualifiedName(), packagename, "class");
//						}
//						if(classNode.checkNodeExist(nodeName, nodePackageName, "class")){
//						}else{							
//						    classNode.createNode(node.getName().getFullyQualifiedName(), nodePackageName, "class");
//						}
						System.out.println("\n");
					}
					classNode.setParseState();
					}
					return true;
				}
//		
  

				//Show method invocation 
				public boolean visit(MethodInvocation node){
					String MCallLexeme = node.getName().getIdentifier();
					// generation of fully qualified method name of API call
					System.out.println("MethodInvocation_Name:"+MCallLexeme);
					try {
						if(node.getExpression()==null){
							System.out.println("MethodInvocation_ExpType:");
							String invoked_method_name;
							ArrayList<String> paramList = new ArrayList<String>();
							for(ITypeBinding iter : node.resolveMethodBinding().getParameterTypes()){
								paramList.add(iter.getQualifiedName());
								System.out.println("paramList:"+iter.getQualifiedName());
								}
							String [] paramArray;
							paramArray=(String[])paramList.toArray(new String[paramList.size()]);
							invoked_method_name = MCallLexeme;
							if(invokedMethod.checkNodeExist(invoked_method_name, paramArray, classNode)){
							}else{
								invokedMethod.createNode(invoked_method_name, paramArray);
								invokedMethod.setContainRelToMethod(classNode);
							}
							if(methodNode.thisNode!=null){
							invokedMethod.setInvokeRelToMethod(methodNode);
							}
						}else{
							String invokedClassName = node.getExpression().resolveTypeBinding().getName();
							String invokedPackageName = node.getExpression().resolveTypeBinding().getPackage().getName();
							ArrayList<String> paramList = new ArrayList<String>();
							for(ITypeBinding iter : node.resolveMethodBinding().getParameterTypes()){
								paramList.add(iter.getQualifiedName());
								System.out.println("paramList:"+iter.getQualifiedName());
								}
							String [] paramArray;
							paramArray=(String[])paramList.toArray(new String[paramList.size()]);
							
							//Wait to be modified to judge that it is a class or interface
							if(invokedClass.checkNodeExist(invokedClassName, invokedPackageName, "class")){								
							}else{
								invokedClass.createNode(invokedClassName, invokedPackageName, "class");
							}
							invokedClass.setUseRelToClass(classNode);
							if(invokedMethod.checkNodeExist(MCallLexeme, paramArray,invokedClass)){
							}else{
								invokedMethod.createNode(MCallLexeme, paramArray);
								invokedMethod.setContainRelToMethod(invokedClass);
							}
							
							if(methodNode.thisNode!=null){
							invokedMethod.setInvokeRelToMethod(methodNode);
							}
//							System.out.println("MethodInvocation_Expression:"+node.getExpression());
							
							//Wait to be modified 
							//How to judge different methods?
//							if(invokedMethod.checkNodeExist(MCallLexeme)){
//								invokedMethod.setInvokeRelToMethod(methodNode);
//								invokedClass.createNode(node.getExpression().resolveTypeBinding().getName(),node.getExpression().resolveTypeBinding().getPackage().getName(),"class");
//								invokedClass.setUseRelToClass(classNode);
//							}else{
//								invokedMethod.createNode(MCallLexeme);
//								invokedMethod.setInvokeRelToMethod(methodNode);
//								invokedClass.createNode(node.getExpression().resolveTypeBinding().getName(),node.getExpression().resolveTypeBinding().getPackage().getName(),"class");
//								invokedClass.setUseRelToClass(classNode);
////							}
//							for(ITypeBinding param_name : node.resolveMethodBinding().getTypeParameters()){
//								System.out.println("MethodInvocation_param_name:"+param_name.getQualifiedName());
//							}
//							for(ITypeBinding iter : node.resolveMethodBinding().getMethodDeclaration().getParameterTypes()){
//								System.out.println("getTypeParameters:"+iter.getQualifiedName());
//							}
//							System.out.println("getTypeParameters:"+node.resolveMethodBinding().getMethodDeclaration().);
							System.out.println("MethodInvocation_ExpType_ClassName:"+node.getExpression().resolveTypeBinding().getName());
							System.out.println("MethodInvocation_ExpType_PackageName:"+node.getExpression().resolveTypeBinding().getPackage().getName()+"\n");
						}
//							System.out.println('\n');
					} catch(Exception e) {
						System.out.println(e);
					}
					if(methodNode.thisNode!=null){
					methodNode.setParseState();
					}
					return true; // do not continue
				}
	
				//show method declaration
				public boolean visit(MethodDeclaration node){
					 System.out.println("\nMethodDeclaration:");
//					 MethodNode methodnode = new MethodNode();
				     String methodName=node.getName().getFullyQualifiedName();  
				     System.out.println("method name:"+methodName); 
//				     System.out.println(node.resolveBinding().);
				            //get method parameters  
				     String [] param;
				     ArrayList<String> tempParam = new ArrayList<String>();
				     try{
					     for(ITypeBinding iter : node.resolveBinding().getParameterTypes()){
					    	if(!iter.getQualifiedName().equals(null))
					    	tempParam.add(iter.getQualifiedName());
					     } 
				     }catch(Exception e){
				    	 System.out.println("There is a exception because of inner class!");
				    	 return true;
				     }
				     param = (String[])tempParam.toArray(new String[tempParam.size()]);
				     System.out.println("method parameters:"+param); 
				     if(methodNode.checkNodeExist(methodName, param, classNode)){
					     methodNode.setContainRelToMethod(classNode);
				     }else{
				    	 methodNode.createNode(methodName,param);
				    	 methodNode.setContainRelToMethod(classNode);
				     }
//				     for(Object iter : param){
//				    	 iter.
//				     }
				            //get method return type  
				     Type returnType=node.getReturnType2();  
				     System.out.println("method return type:"+returnType); 
				            
				            //get method modifiers
				     List modifiers=node.modifiers();
				     System.out.println("method modifiers:" + modifiers);
				     System.out.println("\n");
					return true;
}
//					 			
			});
			  		
	    	super.acceptAST(sourceFilePath, ast);
	}
	
}
