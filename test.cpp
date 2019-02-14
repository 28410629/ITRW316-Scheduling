
// CPP program to demonstrate multithreading 
// using three different callables. 
#include <iostream> 
#include <thread> 
#include <mutex>
using namespace std; 

// mutex used to lock other threads from gaining access to shared resource
mutex m_mutex;

// shared print function for cout
void shared_print(char c, int v) {
	m_mutex.lock();
	cout << c << v << "\n";
	m_mutex.unlock();
}

// function for sequence
void foo(char d, int a) {
	for (int i = 1; i <= a; i++) {
      		shared_print(d, i);
	}
}	

int main() 
{ 
	thread th2(foo, 'B', 20); // child thread - 1
	thread th3(foo, 'C', 20); // child thread - 2
	thread th4(foo, 'D', 20); // child thread - 3
	foo('A', 20); // main thread
	//th2.detach();
	//th3.detach();
	//th4.detach();
	th2.join(); // main thread, waits for child to finish
	th3.join(); // main thread, waits for child to finish
	th4.join(); // main thread, waits for child to finish
	return 0; 
} 

