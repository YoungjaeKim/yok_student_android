package bigcamp.yok.student;

/**
 * Created by Youngjae on 13. 8. 31.
 */
public enum YokUrl {
	// http://stackoverflow.com/questions/3978654/java-string-enum 참고.

	ROOT(""),
	TOKEN("/token"), LOGIN("/login"), REGISTER("/register"); // 토큰스티커

	public static String BaseUrl = "something.cloudapp.net";
	public static String BaseRestUrl = "something.cloudapp.net/rest";
	public static String ApplicationName = "하지말아욕";

	/**
	 * 생성자.
	 *
	 * @param text
	 */
	private YokUrl(final String text) {
		this.text = text;
	}

	private final String text;

	/**
	 * 연결 주소를 HTTP로 반환. (HTTPS 주소는 {@code toString(true)}를 이용.
	 *
	 * @return
	 */
	public String toString() {
		return this.toString(false);
	}

	/**
	 * 연결 주소를 반환.
	 *
	 * @param isSecure {@code true} 면 HTTPS, {@code false}면 HTTP.
	 * @return
	 */
	public String toString(Boolean isSecure) {
		return (isSecure ? "https://" : "http://") + BaseRestUrl + text;
	}

	public String toString(Boolean isSecure, Boolean isIncludeRest) {
		return (isSecure ? "https://" : "http://") + (isIncludeRest ? BaseRestUrl : BaseUrl) + text;
	}
}
