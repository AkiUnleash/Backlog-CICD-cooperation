package auth

import pdi.jwt.{Jwt, JwtAlgorithm}

/** Processing on Jwt Authentication */
trait JwtAuthentication {
  private val securityKey = "SIIIII"

  /** Encoding of Jwt.
   *
   * @param data Original data to be encoded.
   */
  def jwtEncode(data: String): String = {
    Jwt.encode(data, securityKey, JwtAlgorithm.HS256)
  }

  /** Encoding of Jwt.
   *
   * @param data Original data to be decoded.
   */
  def jwtDecode (data: String): String = {
    val result = Jwt.decodeRaw(data, securityKey, Seq(JwtAlgorithm.HS256))
    val s"Success($uuid)" = result.toString()
    uuid
  }
}
