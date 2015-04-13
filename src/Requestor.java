import java.util.ArrayList;

import org.eclipse.jdt.core.dom.*;

//test Ϊ�˲��Բ�����İ�
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
				// ����Project��Enum��contain��ϵ
				ParseMain.containClassMap.addOneRel(
						ParseMain.projectNodeMap.lastRecentNode,
						ParseMain.classNodeMap.getNode(nodeName,
								nodePackageName, "enum"));
				// ����Enum����ʵ�ֵ�Interface��implement��ϵ
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

				// ���� ��ӡ���й�ϵ �� ���нڵ�
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
				// ��ǰ����������������һ��interface
				if (node.isInterface() == true) {
					// ע�⣬��������������������ڵ��ʱ����Ҫ��sourceFilePath���뵽�ڵ㵱��
					if (!ParseMain.classNodeMap.checkNodeExist(nodeName,
							nodePackageName, "interface"))
						ParseMain.classNodeMap.addNode(nodeName,
								nodePackageName, "interface", sourceFilePath);
					ParseMain.classNodeMap.getNode(nodeName, nodePackageName,
							"interface").fileLocation = sourceFilePath;
					// ������ǰ������project��contain��ϵ
					ParseMain.containClassMap.addOneRel(
							ParseMain.projectNodeMap.lastRecentNode,
							ParseMain.classNodeMap.getNode(nodeName,
									nodePackageName, "interface"));
					if (node.getSuperclassType() != null) {
						// ������ǰ���������ļ̳й�ϵ
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
					// ��ǰ����������������һ��class
					ITypeBinding binding = null;
					binding = node.resolveBinding();
					if (!binding.isNested()) {
						// ��ǰ������������������һ���ڲ���
						// ע�⣬��������������������ڵ��ʱ����Ҫ��sourceFilePath���뵽�ڵ㵱��
						if (!ParseMain.classNodeMap.checkNodeExist(nodeName,
								nodePackageName, "class"))
							ParseMain.classNodeMap.addNode(nodeName,
									nodePackageName, "class", sourceFilePath);
						ParseMain.classNodeMap.getNode(nodeName,
								nodePackageName, "class").fileLocation = sourceFilePath;
						// ������ǰ������project��contain��ϵ
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
				// �洢����String������
				String[] params;
				// �����в���String����һ��String
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
				// ���￴�����е�� ΪʲôҪ��toArray�������������ô��������
				params = (String[]) tempParam.toArray(new String[tempParam
						.size()]);
				// ��String[]ƴ��һ��String
				for (String iter : params) {
					param = param.concat(iter);
				}
				// ��鵱ǰ�����ķ����ڵ��Ƿ񱻽�����
				if (!ParseMain.methodNodeMap.checkNodeExist(nodeName, param,
						ParseMain.classNodeMap.lastRecentDecClassNode
								.getString()))
					ParseMain.methodNodeMap.addNode(nodeName, param,
							ParseMain.classNodeMap.lastRecentDecClassNode
									.getString());
				// ������ǰ������������������contain��ϵ
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
				// nodeName�Ǳ����÷���������
				String nodeName = node.getName().getIdentifier();
				// �洢������String������
				String[] params;
				// �����еĲ�������һ��String
				String param = "";
				// ��÷������õĲ���
				try {
					ArrayList<String> paramList = new ArrayList<String>();
					for (ITypeBinding iter : node.resolveMethodBinding()
							.getParameterTypes()) {
						paramList.add(iter.getQualifiedName());

					}
					params = (String[]) paramList.toArray(new String[paramList
							.size()]);
					// ��String[]ƴ��һ��String
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
						// ���´�����õ�ǰ���static����
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
						// Ϊ��ǰ���������ͱ����õķ�������invoke��ϵ
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
						// ��鱻���õķ���������class�Ƿ����,�������򴴽�
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
						// Ϊ��ǰ�������ͱ����õķ������ڵ��ཨ��use��ϵ
						ParseMain.useClassMap.addOneRel(
								ParseMain.classNodeMap.lastRecentDecClassNode,
								ParseMain.classNodeMap.getNode(
										invokedClassName, invokedPackageName,
										node.getExpression()
												.resolveTypeBinding()
												.isInterface() ? "interface"
												: "class"));
						// Ϊ�����õķ����������ڵ��ཨ��contain��ϵ
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
						// Ϊ��ǰ���������ͱ����õķ�������invoke��ϵ
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
