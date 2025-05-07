import SwiftUI
import Shared
import ModuleA
import ModuleB

struct ContentView: View {
    @State private var showContent = false

    var body: some View {
        VStack(spacing: 8) {
            //Different modules
            let moduleA = ClassFromA(name:"class_A")
            Text("Module A: \(moduleA.hello())")
            let moduleB = ClassFromB(name:"class_B")
            Text("Module B: \(moduleB.hello()) ")

            //Typealias
            let myClass = MyClass(property: 5)
            let nestedClass = MyNested(nestedProperty: 6)
            Text("Type alias class is \(nestedClass.nestedProperty)")

            //Top-level function
            Text("The sum is: \(sum(a: myClass, b: nestedClass))")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        .padding()
    }
    
    func testSamples(){
        let _ = repeated("Hello! ", times: 3)
        let _ = getLen("Hello")
        overloaded(x: "hello")
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
