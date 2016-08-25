package metrics;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.atlas.java.core.script.Common;

/**
 * A class of helper methods for calculating various software package metrics
 * See https://en.wikipedia.org/wiki/Software_package_metrics
 */
public class Metrics {

	/**
	 * Return a the number of nodes in the given graph
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static long countNodes(Q graph){
		return graph.eval().nodes().size();
	}
	
	/**
	 * Return a Q of only nodes that are packages in the given graph
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getPackages(Q graph){
		return graph.nodesTaggedWithAll(XCSG.Package);
	}
	
	/**
	 * Returns the packages of a set of program elements
	 * @param programElements
	 * @return
	 */
	public static Q getPackagesOfProgramElements(Q programElements){
		// Step 1) Create a subgraph of the universe containing the contains edges
		Q containsEdges = Common.universe().edgesTaggedWithAny(XCSG.Contains);
		// Step 2) Starting from each program element, traverse backwards along contains edges and select package nodes
		return containsEdges.reverse(programElements).nodesTaggedWithAny(XCSG.Package);
	}
	
	/**
	 * Return a Q of only nodes that are types (classes, abstract classes, and interfaces) in the given graph
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getTypes(Q graph){
		return graph.nodesTaggedWithAll(XCSG.Type);
	}
	
	/**
	 * Return a Q of only nodes that are classes in the given graph
	 * Note that classes may be abstract or concrete, do not include interfaces
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getClasses(Q graph){
		// TODO: Implement
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Return a Q of only nodes that are interfaces in the given graph
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getInterfaces(Q graph){
		// TODO: Implement
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Return a Q of only nodes that are classes that are abstract in the given graph
	 * Note: Include interfaces since they are technically abstract classes
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getAbstractClasses(Q graph){
		// TODO: Implement
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Return a Q of only the concrete classes (all classes that are not interfaces or abstract classes) in the given graph
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getConcreteClasses(Q graph) {
		// TODO: Implement
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Return a Q of only nodes that are methods in the given graph
	 * @param graph A subgraph of the universe
	 * @return
	 */
	public static Q getMethods(Q graph){
		// TODO: Implement
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Returns the methods declared under a given package.  Includes methods in inner classes (classes declared within classes).
	 * @param pkg A single package node
	 * @return
	 */
	public static Q getPackageMethods(Node pkg){
		// TODO: Implement
		Q packages = Common.toQ(pkg);
		// Step 1) Create a subgraph of CONTAINS edges from the universe
		// Step 2) Within the contains subgraph traverse forward and select only the METHOD nodes from the result
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Returns the classes and interfaces declared under a given package.  Includes inner classes and interfaces (classes/interfaces declared within classes).
	 * @param pkg A single package node
	 * @return
	 */
	public static Q getPackageTypes(Node pkg){
		Q packages = Common.toQ(pkg);
		// Step 1) Create a subgraph of CONTAINS edges from the universe to find the declared classes of the package
		Q containsEdges = Common.universe().edgesTaggedWithAny(XCSG.Contains).retainEdges();
		// Step 2) Within the contains subgraph traverse forward and select only the TYPE (classes and interfaces) nodes from the result
		return getTypes(containsEdges.forward(packages));
	}

	/**
	 * Calculates the ratio of the number of abstract classes (and interfaces) in the package to the total number of types in the package
	 * @param pkg A single package node
	 */
	public static double getAbstractness(Node pkg) throws ArithmeticException {
		// Step 1) Get the types declared under the package
		Q packageTypes = getPackageTypes(pkg);
		// Step 2) From the discovered class nodes select the abstract classes
		Q abstractClasses = getAbstractClasses(packageTypes);
		// Step 4) Return the ratio of the number of abstract classes (and interfaces) in the package to the total number of types in the package
		return (double) countNodes(abstractClasses) / (double) countNodes(packageTypes);
	}
	
	/**
	 * Returns the packages classes outside the given package that depend upon classes inside the package
	 * A dependency is that a package class makes calls to a class in another package
	 * @param pkg A single package node
	 * @return
	 */
	public static Q getDependentPackages(Node pkg){
		// TODO: Implement
		// Step 1) Get the methods declared under the package
		// Step 2) Create a subgraph of CALL edges from the universe
		// Step 3) Within the calls subgraph get the predecessors of the package methods (calling methods)
		// Step 4) Return the packages of the calling methods
		throw new RuntimeException("Not Implemented!");
	}
	
	/**
	 * Returns the packages of classes outside the given package that classes inside the package depend upon
	 * A dependency is that a package class makes calls to a class in another package
	 * @param pkg A single package node
	 * @return
	 */
	public static Q getPackageDependencies(Node pkg){
		// TODO: Implement
		// Step 1) Get the methods declared under the package
		// Step 2) Create a subgraph of CALL edges from the universe
		// Step 3) Within the calls subgraph get the successors of the package methods (called methods)
		// Step 4) Return the packages of the called methods
		throw new RuntimeException("Not Implemented!");
	}

	/**
	 * Calculates the afferent coupling (Ca): The classes outside the package that depend upon classes inside the package.
	 * A dependency is that a package class makes calls to a class in another package
	 * @param pkg A single package node
	 * @return
	 */
	public static Q getAfferentCouplings(Node pkg) {
		// return getDependentPackages(pkg); // includes packages in Java APIs
		return getDependentPackages(pkg).intersection(SetDefinitions.app());
	}

	/**
	 * Calculates the efferent coupling (Ce): The classes outside the package that classes inside the package depend upon.
	 * A dependency is that a package class makes calls to a class in another package
	 * @param pkg A single package node
	 * @return
	 */
	public static Q getEfferentCouplings(Node pkg) {
//		return getPackageDependencies(pkg)); // includes packages in Java APIs
		return getPackageDependencies(pkg).intersection(SetDefinitions.app());
	}

	/**
	 * Calculates the instability Ce/(Ca+Ce).  Range [0,1] where 0 is very stable and 1 is very unstable.
	 * @param pkg A single package node
	 * @return
	 */
	public static double getInstability(Node pkg) {
		double ce = new Double(countNodes(getEfferentCouplings(pkg)));
		double ca = new Double(countNodes(getAfferentCouplings(pkg)));
		return ce / (ca + ce);
	}
}