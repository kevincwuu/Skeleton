package adambl4.instamotorsskeleton.utils.extensions

import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.then

/**
 * Created by Adambl4 on 27.04.2016.
 */

/*Partial application for one param function*/
operator fun <P1, R> Function1<P1, R>.invoke(p: P1): () -> R {
    return { this(p) }
}

infix fun<P1, IP, R> Function1<P1, Promise<IP, Exception>>.andThen(f: (IP) -> R): (P1) -> R {
    return { p1: P1 ->
        this(p1).then {
            f(it)
        }.get()
    }
}

/*infix fun<IP, R> Function0<Promise<IP, Exception>>.andThen(f: (IP) -> R): (IP) -> R {
    return {
        this().then {
            f(it)
        }.get()
    }
}*/

// () -> IP |> (IP) -> R
infix fun<IP, R> Function0<IP>.andThen(f: (IP) -> R): () -> R {
    return { f(this()) }
}


// () -> Promise<IP, Exception> |> (IP) -> R
infix fun<IP, R> Function0<Promise<IP, Exception>>.promiseThen(f: (IP) -> R): () -> R {
    return {
        this().then {
            f(it)
        }.get()
    }
}





