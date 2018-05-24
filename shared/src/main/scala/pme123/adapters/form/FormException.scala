package pme123.adapters.form

trait FormException
  extends RuntimeException {
  def msg: String

  def cause: Option[Throwable] = None

  override def getMessage: String = msg

  override def getCause: Throwable = {
    cause.orNull
  }
}

case class MapException(msg: String, override val cause: Option[Throwable] = None)
  extends FormException

case class EncoderException(msg: String, override val cause: Option[Throwable] = None)
  extends FormException

case class DecoderException(msg: String, override val cause: Option[Throwable] = None)
  extends FormException