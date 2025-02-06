# Resolución Laboratorio 1

## Tabla de Contenido
1. [Integrantes](#integrantes)
2. [Introducción](#introducción)
3. [Procedimiento](#procedimiento)
    1. [Parte I](#Parte-i)
    2. [Parte II](#parte-ii)
4. [Conclusiones](#conclusiones)

---

## Integrantes
- Santiago Avellaneda
- Miguel Motta

---

## Introducción
[Aquí va la introducción]

---

## Procedimiento
---

### Parte I 
>
> 1. Descargue el proyecto
     [*PrimeFinder*](https://github.com/ARSW-ECI/wait-notify-excercise).
     Este es un programa que calcula números primos entre 0 y M
     (Control.MAXVALUE), concurrentemente, distribuyendo la búsqueda de
     los mismos entre n (Control.NTHREADS) hilos independientes.
>
>
> 2.  Se necesita modificar la aplicación de manera que cada t
      milisegundos de ejecución de los threads, se detengan todos los
      hilos y se muestre el número de primos encontrados hasta el momento.
      Luego, se debe esperar a que el usuario presione ENTER para reanudar
      la ejecución de los mismos. Utilice los mecanismos de sincronización
      provistos por el lenguaje (wait y notify, notifyAll).

### Solución
> Para resolver este ejercicio, es importante tener claro el concepto de hilo y
> los mecanismos que utilizan sus métodos:
> * [`wait()`](https://www.arquitecturajava.com/java-wait-notify-y-threads/)
> * [`notify()`](https://www.arquitecturajava.com/java-wait-notify-y-threads/)
> * [`notifyAll()`](https://www.geeksforgeeks.org/difference-between-notify-and-notifyall-in-java/)


Ya tuvimos la oportunidad de implementar hilos, sin embargo, no habíamos usado
uno de sus métodos fuera de [`join()`](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html).
A diferencia de `join()`, estos métodos deben ejecutarse en un contexto [`synchronized`](https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html)
que puede declararse como un método o contenido en un método. `synchronized` es una herramienta que nos permite ejecutar código
secuencialmente en un bloque de hilos, esto nos permite alterar el comportamiento de nuestros hilos para ejecutar
tareas a priori y tener un control sobre estos. Esta declaración se hace con un objeto que funciona como
un monitor del hilo en cuestión.

Un objeto monitor es un mecanismo de sincronización que controla el acceso concurrente de un objeto (o hilo en este caso).
Con estos objetos podemos hacer uso de los métodos `wait()`, `notify()` y `notifyAll()`, teniendo en cuenta,
que los hilos deben compartir este objeto monitor en común para funcionar correctamente.

Teniendo en cuenta lo anterior, la estrategia que seguimos para resolver el ejercicio, fue basándonos en el 
concepto de cómo funcionan los hilos y la sincronización. 

Creamos un objeto final que va a funcionar cómo monitor, este objeto se lo agregamos como atributo final
a la clase `Control` y a la clase `PrimeFinderThread`

#### *Clase de Control*
```
public class Control extends Thread {
    
    // Attributes ...
    private volatile boolean paused = false;
    private final Object monitor = new Object();
    // Contructor and Methods ...
}
```

#### *Clase de PrimeFinderThread*
```
public class PrimeFinderThread extends Thread{
    // Attributes ...
    private final Control controller;
    private final Object monitor;
    // Contructor and Methods ...
}    
```

El objeto `monitor` nos permite controlar los hilos dado que es un recurso compartido
que nos permitirá establecer un bloqueo de los hilos en un estado dado.

Siguiendo con esta estrategia, podemos ver que también hay un atributo de `controller` de tipo `Controller`
esto con el fin de permitir una comunicación bidireccional entre las clases. 

El comportamiento que se encuentra en la clase de `PrimeFinderThread` ejecuta el proceso que tenía
definido previamente, pero haciendo primero una confirmación de estado sobre un atributo cambiante o 
'volátil' que nos permite confirmar que el proceso esté pausado. Esto podría evitarse si pudiéramos
garantizar que los procesos que usan el método `wait()` quedan pausados indefinidamente, sin embargo, dada la
naturaleza de Java, se pueden presentar casos extraños en los que un hilo se 'active' solo, sin haber sido
llamado con el método para activarlo `notify()` o `notifyAll()`. Por esta razón, se recomienda declarar el método `wait()` 
dentro de una sentencia `while` para garantizar un comportamiento controlado, lo hicimos de la siguiente manera:

#### *Clase de PrimeFinderThread*
```
public class PrimeFinderThread extends Thread{
    // Attributes ...
    // Contructor
    @Override
        public void run() {
            for (int i = a; i < b; i++) {
                synchronized (monitor) {
                    while (controller.isPaused()) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }
                }

                if (isPrime(i)) {
                    primes.add(i);
                    System.out.println(i);
                }
            }
        }
    // More methods ...
}    
```
Cómo podemos ver, se declara `synchronized (monitor)` sabiendo qué es el objeto `monitor`. 
Además, en el ciclo `while` se hace una verificación con el controlador, si este
está en pausa, el hilo de tipo `PrimeFinderThread` ejecutará el contenido del ciclo `while` 
que detiene el mismo hilo y lo pone en espera para que otro hilo lo active.
De lo contrario determinará si el número del ciclo es primo, y de serlo, lo imprimirá.

Con esta modificación podemos establecer este comportamiento en el controlador sin 
necesidad de ejecutar el método `wait()`, sino cambiar el estado de la siguiente manera:

#### *Clase de Control*
```
public class Control extends Thread {
    // Attributes ...
    // Contructor and Methods ...
    void pauseAll(){
        synchronized (monitor) {
            paused = true;
        }
    }
}
```
Este método desencadenará que los demás hilos se pausen, pero él seguirá
en su ejecución independiente.

Ahora para reanudar los hilos, podemos aprovechar el atributo en común que comparten los
hilos que se están controlando, en un método implementando el comando `notifyAll()`:

#### *Clase de Control*
```
public class Control extends Thread {
    // Attributes ...
    // Contructor and Methods ...
    void resumeThreads(){
        synchronized (monitor){
            paused = false;
            monitor.notifyAll();
        }
    }
}
```

De esta forma, se reanudarán todos los hilos que compartan el objeto `monitor` en común.


#### Parte II
TO DO

## Conclusiones
TO DO