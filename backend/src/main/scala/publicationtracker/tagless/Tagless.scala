package publicationtracker.tagless

// Функция-преобразователь между контекстами F[_] и G[_]
trait FunctionK[F[_], G[_]] {
  def apply[A](fa: F[A]): G[A]
}

// Тип класса для HKD — возможность преобразовать F[_] => G[_] внутри F[_[_]]
trait FunctorK[F[_[_]]] {
  def mapK[G[_], H[_]](af: F[G])(fk: FunctionK[G, H]): F[H]
}