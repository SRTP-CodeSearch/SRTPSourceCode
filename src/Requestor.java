import java.util.ArrayList;

import org.eclipse.jdt.core.dom.*;

//test 为了测试才引入的包
import Node.ClassNode;
import Node.RelMap.PairsOfNode;

public class Requestor extends FileASTRequestor {
	public static String projectName;
	public static int enumBindingSuccess;
	public static int enumBindingException;
	public static int TypeBindingSuccess;
	public static int TypeBindingException;
	public static int superBindingSuccess;
	public static int superBindingException;
	public static int methodBindingSuccess;
	public static int methodBindingException;
	public static int methodInvokeBindingSuccess;
	public static int methodInvokeBindingException;
	public static int staticBindingSuccess;
	public static int staticBindingException;
	public String fileLocation;

	public Requestor(String projectName, String fileLocation) {
		if (!ParseMain.projectNodeMap.checkNodeExist(projectName, fileLocation))
			ParseMain.projectNodeMap.addNode(projectName, fileLocation);
	}

	public void acceptAST(final String sourceFilePath, CompilationUnit ast) {
		ast.accept(new ASTVisitor() {
			{
				System.out.println(sourceFilePath);
			}

			public boolean visit(EnumDeclaration node) {
				String nodeName = node.getName().getFullyQualifiedName();
				String nodePackageName;
				try {
					ITypeBinding binding=node.resolveBinding();
					if(binding.isNested()){
						return true;
					}
					nodePackageName = node.resolveBinding().getPackage()
							.getName();
					enumBindingSuccess++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out
							.println("Typebinding product a exception for Enum Declaration");
					enumBindingException++;
					return true;
				}
				if (!ParseMain.classNodeMap.checkNodeExist(nodeName,
						nodePackageName, "enum"))
					ParseMain.classNodeMap.addNode(nodeName, nodePackageName,
							"enum", sourceFilePath);
				ParseMain.classNodeMap.getNode(nodeName, nodePackageName,
						"enum").fileLocation = sourceFilePath;
				// 建立Project和Enum的contain关系
				ParseMain.containClassMap.addOneRel(
						ParseMain.projectNodeMap.lastRecentNode,
						ParseMain.classNodeMap.getNode(nodeName,
								nodePackageName, "enum"));
				// 建立Enum和其实现的Interface的implement关系
				if (!node.superInterfaceTypes().isEmpty()) {
					for (ITypeBinding iter1 : node.resolveBinding()
							.getInterfaces()) {
						String implemInterfaceName = iter1.getName();
						String implemPackageName = node.resolveBinding()
								.getPackage().getName();
						if (!ParseMain.classNodeMap.checkNodeExist(
								implemInterfaceName, implemPackageName,
								"interface"))
							ParseMain.classNodeMap.addNode(implemInterfaceName,
									implemPackageName, "interface");
						ParseMain.impleInterfaceMap.addOneRel(nodeName
								+ nodePackageName, implemInterfaceName
								+ implemPackageName);
					}
				}
				ParseMain.classNodeMap.getNode(nodeName, nodePackageName,
						"enum").parseState = true;

				// 测试 打印所有关系 和 所有节点
				// for(PairsOfNode iter : ParseMain.containClassMap.getAll()){
				// System.out.println(ParseMain.projectNodeMap.getNode(iter.getNode1Index())+"-----contain---->"+ParseMain.classNodeMap.getNode(iter.getNode2Index()));
				// }
				// ParseMain.projectNodeMap.printAllNodes();
				// ParseMain.classNodeMap.printAllNodes();
				return true;
			}

			public boolean visit(TypeDeclaration node) {
				String nodeName = node.getName().getFullyQualifiedName();
				String nodePackageName;
				try {
					ITypeBinding binding=node.resolveBinding();
					if(binding.isNested()){
						return true;
					}
					nodePackageName = node.resolveBinding().getPackage()
							.getName();
					TypeBindingSuccess++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out
							.println("Typebinding product a exception for Type Declaration");
					TypeBindingException++;
					return true;
				}
				// 当前解析的类型声明是一个interface
				if (node.isInterface() == true) {
					// 注意，在这里对类型声明建立节点的时候，需要把sourceFilePath加入到节点当中
					if (!ParseMain.classNodeMap.checkNodeExist(nodeName,
							nodePackageName, "interface"))
						ParseMain.classNodeMap.addNode(nodeName,
								nodePackageName, "interface", sourceFilePath);
					ParseMain.classNodeMap.getNode(nodeName, nodePackageName,
							"interface").fileLocation = sourceFilePath;
					// 建立当前类型与project的contain关系
					ParseMain.containClassMap.addOneRel(
							ParseMain.projectNodeMap.lastRecentNode,
							ParseMain.classNodeMap.getNode(nodeName,
									nodePackageName, "interface"));
					if (node.getSuperclassType() != null) {
						// 建立当前类型声明的继承关系
						String extendClassName;
						String extendClassPackageName;
						try {
							extendClassName = node.getSuperclassType()
									.resolveBinding().getName();
							extendClassPackageName = node.getSuperclassType()
									.resolveBinding().getPackage().getName();
							superBindingSuccess++;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out
									.println("Typebinding of extendclass product a exception for Type Declaration");
							superBindingException++;
							return true;
						}
						if (!ParseMain.classNodeMap.checkNodeExist(
								extendClassName, extendClassPackageName,
								"interface"))
							ParseMain.classNodeMap.addNode(extendClassName,
									extendClassPackageName, "interface");
						// ParseMain.extendClassMap.addOneRel(nodeName+nodePackageName,extendClassName+extendClassPackageName);
						ParseMain.extendClassMap.addOneRel(
								ParseMain.classNodeMap.getNode(nodeName,
										nodePackageName, "interface"),
								ParseMain.classNodeMap.getNode(extendClassName,
										extendClassPackageName, "interface"));

					}
					ParseMain.classNodeMap.lastRecentDecClassNode = ParseMain.classNodeMap
							.getNode(nodeName, nodePackageName, "interface");
					ParseMain.classNodeMap.getNode(nodeName, nodePackageName,
							"interface").parseState = true;
				} else {
					// 当前解析的类型声明是一个class
					ITypeBinding binding = null;
					binding = node.resolveBinding();
					if (!binding.isNested()) {
						// 当前解析的类型声明不是一个内部类
						// 注意，在这里对类型声明建立节点的时候，需要把sourceFilePath加入到节点当中
						if (!ParseMain.classNodeMap.checkNodeExist(nodeName,
								nodePackageName, "class"))
							ParseMain.classNodeMap.addNode(nodeName,
									nodePackageName, "class", sourceFilePath);
						ParseMain.classNodeMap.getNode(nodeName,
								nodePackageName, "class").fileLocation = sourceFilePath;
						// 建立当前类型与project的contain关系
						ParseMain.containClassMap.addOneRel(
								ParseMain.projectNodeMap.lastRecentNode,
								ParseMain.classNodeMap.getNode(nodeName,
										nodePackageName, "class"));
						if (node.getSuperclassType() != null) {
							try {
								String extendClassName = node.getSuperclassType()
										.resolveBinding().getName();
								String extendClassPackageName = node
										.getSuperclassType().resolveBinding()
										.getPackage().getName();
								if (!ParseMain.classNodeMap.checkNodeExist(
										extendClassName, extendClassPackageName,
										"class"))
									ParseMain.classNodeMap.addNode(extendClassName,
											extendClassPackageName, "class");
								ParseMain.extendClassMap.addOneRel(
										ParseMain.classNodeMap.getNode(nodeName,
												nodePackageName, "class"),
										ParseMain.classNodeMap.getNode(
												extendClassName,
												extendClassPackageName, "class"));
								superBindingSuccess++;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out
								.println("Typebinding of extendclass product a exception for Type Declaration");
						superBindingException++;
								return true;
							}
							// ParseMain.extendClassMap.addOneRel(nodeName+nodePackageName,extendClassName+extendClassPackageName);
						}
						if (!node.superInterfaceTypes().isEmpty()) {
							for (ITypeBinding iter : node.resolveBinding()
									.getInterfaces()) {
								String implemInterfaceName = iter.getName();
								String implemInterfacePackageName = iter
										.getPackage().getName();
								if (!ParseMain.classNodeMap
										.checkNodeExist(implemInterfaceName,
												implemInterfacePackageName,
												"interface"))
									ParseMain.classNodeMap.addNode(
											implemInterfaceName,
											implemInterfacePackageName,
											"interface");
								// ParseMain.impleInterfaceMap.addOneRel(nodeName+nodePackageName,
								// implemInterfaceName+implemInterfacePackageName);
								ParseMain.impleInterfaceMap.addOneRel(
										ParseMain.classNodeMap.getNode(
												nodeName, nodePackageName,
												"class"),
										ParseMain.classNodeMap.getNode(
												implemInterfaceName,
												implemInterfacePackageName,
												"interface"));
							}
						}
						ParseMain.classNodeMap.lastRecentDecClassNode = ParseMain.classNodeMap
								.getNode(nodeName, nodePackageName, "class");
						ParseMain.classNodeMap.getNode(nodeName,
								nodePackageName, "class").parseState = true;
					}
				}
				return true;
			}

			public boolean visit(MethodDeclaration node) {
				String nodeName = node.getName().getFullyQualifiedName();
				// 存储参数String的数组
				String[] params;
				// 将所有参数String连成一个String
				String param = "";
				ArrayList<String> tempParam = new ArrayList<String>();
				try {
					IMethodBinding binding = node.resolveBinding();
					for (ITypeBinding iter : binding.getParameterTypes()) {
						if (!iter.getQualifiedName().equals(null)) {
							tempParam.add(iter.getQualifiedName());
							// mayBe can't get qualifiedName
						}
					}
					methodBindingSuccess++;
				} catch (Exception e) {
					System.out
							.println("There is a exception because of inner class!");
					System.out.println(e.getMessage());
					methodBindingException++;
					return true;
				}
				// 这里看起来有点怪 为什么要在toArray的括号里面加这么个东西？
				params = (String[]) tempParam.toArray(new String[tempParam
						.size()]);
				// 将String[]拼成一个String
				for (String iter : params) {
					param = param.concat(iter);
				}
				// 检查当前解析的方法节点是否被建立过
				if (!ParseMain.methodNodeMap.checkNodeExist(nodeName, param,
						ParseMain.classNodeMap.lastRecentDecClassNode
								.getString()))
					ParseMain.methodNodeMap.addNode(nodeName, param,
							ParseMain.classNodeMap.lastRecentDecClassNode
									.getString());
				// 建立当前方法声明和类声明的contain关系
				ParseMain.containMethodMap.addOneRel(
						ParseMain.classNodeMap.lastRecentDecClassNode,
						ParseMain.methodNodeMap.getNode(nodeName, param,
								ParseMain.classNodeMap.lastRecentDecClassNode
										.getString()));
				ParseMain.methodNodeMap.lastRecentDecMethodNode = ParseMain.methodNodeMap
						.getNode(nodeName, param,
								ParseMain.classNodeMap.lastRecentDecClassNode
										.getString());
				ParseMain.methodNodeMap.getNode(nodeName, param,
						ParseMain.classNodeMap.lastRecentDecClassNode
								.getString()).parseState = true;
				return true;
			}

			public boolean visit(MethodInvocation node) {
				// nodeName是被调用方法的名字
				String nodeName = node.getName().getIdentifier();
				// 存储参数的String数组中
				String[] params;
				// 将所有的参数连成一个String
				String param = "";
				// 获得方法调用的参数
				try {
					ArrayList<String> paramList = new ArrayList<String>();
					for (ITypeBinding iter : node.resolveMethodBinding()
							.getParameterTypes()) {
						paramList.add(iter.getQualifiedName());

					}
					params = (String[]) paramList.toArray(new String[paramList
							.size()]);
					// 将String[]拼成一个String
					for (String iter1 : params)
						param = param.concat(iter1);
					methodInvokeBindingSuccess++;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out
							.println("There is a exception in parsing param!");
					methodInvokeBindingException++;
					e1.printStackTrace();
				}
				try {
					if (node.getExpression() == null) {
						// 以下处理调用当前类的static方法
						if (nodeName.equals("add_ior_interceptor"))
							System.out.println("add_ior_interceptor" + "---"
									+ param);
						if (!ParseMain.methodNodeMap.checkNodeExist(nodeName,
								param,
								ParseMain.classNodeMap.lastRecentDecClassNode
										.getString()))
							ParseMain.methodNodeMap
									.addNode(
											nodeName,
											param,
											ParseMain.classNodeMap.lastRecentDecClassNode
													.getString());
						// 为当前方法声明和被调用的方法建立invoke关系
						ParseMain.invokeMethodMap
								.addOneRel(
										ParseMain.methodNodeMap.lastRecentDecMethodNode,
										ParseMain.methodNodeMap
												.getNode(
														nodeName,
														param,
														ParseMain.classNodeMap.lastRecentDecClassNode
																.getString()));
					} else {
						String invokedClassName = node.getExpression()
								.resolveTypeBinding().getName();
						String invokedPackageName = node.getExpression()
								.resolveTypeBinding().getPackage().getName();
						// 检查被调用的方法所属的class是否存在,不存在则创建
						if (!ParseMain.classNodeMap.checkNodeExist(
								invokedClassName, invokedPackageName, node
										.getExpression().resolveTypeBinding()
										.isInterface() ? "interface" : "class"))
							ParseMain.classNodeMap.addNode(invokedClassName,
									invokedPackageName,
									node.getExpression().resolveTypeBinding()
											.isInterface() ? "interface"
											: "class");
						if (!ParseMain.methodNodeMap.checkNodeExist(
								nodeName,
								param,
								ParseMain.classNodeMap.getNode(
										invokedClassName,
										invokedPackageName,
										node.getExpression()
												.resolveTypeBinding()
												.isInterface() ? "interface"
												: "class").getString()))
							ParseMain.methodNodeMap
									.addNode(
											nodeName,
											param,
											ParseMain.classNodeMap
													.getNode(
															invokedClassName,
															invokedPackageName,
															node.getExpression()
																	.resolveTypeBinding()
																	.isInterface() ? "interface"
																	: "class")
													.getString());
						// 为当前类声明和被调用的方法属于的类建立use关系
						ParseMain.useClassMap.addOneRel(
								ParseMain.classNodeMap.lastRecentDecClassNode,
								ParseMain.classNodeMap.getNode(
										invokedClassName, invokedPackageName,
										node.getExpression()
												.resolveTypeBinding()
												.isInterface() ? "interface"
												: "class"));
						// 为被调用的方法和其属于的类建立contain关系
						ParseMain.containMethodMap.addOneRel(
								ParseMain.classNodeMap.getNode(
										invokedClassName, invokedPackageName,
										node.getExpression()
												.resolveTypeBinding()
												.isInterface() ? "interface"
												: "class"),
								ParseMain.methodNodeMap
										.getNode(
												nodeName,
												param,
												ParseMain.classNodeMap
														.getNode(
																invokedClassName,
																invokedPackageName,
																node.getExpression()
																		.resolveTypeBinding()
																		.isInterface() ? "interface"
																		: "class")
														.getString()));
						// 为当前方法声明和被调用的方法建立invoke关系
						ParseMain.invokeMethodMap
								.addOneRel(
										ParseMain.methodNodeMap.lastRecentDecMethodNode,
										ParseMain.methodNodeMap
												.getNode(
														nodeName,
														param,
														ParseMain.classNodeMap
																.getNode(
																		invokedClassName,
																		invokedPackageName,
																		node.getExpression()
																				.resolveTypeBinding()
																				.isInterface() ? "interface"
																				: "class")
																.getString()));
					}
					staticBindingSuccess++;
				} catch (Exception e) {
					System.out
							.println("There is a exception in MethodInvocation!");
					staticBindingException++;
					e.printStackTrace();
				}
				return true;
			}
		});
		super.acceptAST(sourceFilePath, ast);
	}
}
