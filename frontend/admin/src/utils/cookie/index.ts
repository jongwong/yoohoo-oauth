/**
 * 设置 Cookie 的选项
 */
interface CookieOptions {
    maxAge?: number; // 相对于当前时间的秒数
    expires?: string; // 绝对过期时间 (GMT 格式)
    path?: string; // Cookie 的作用路径
    secure?: boolean; // 是否仅在 HTTPS 下发送
    sameSite?: "Strict" | "Lax" | "None"; // SameSite 策略
}

/**
 * 设置 Cookie
 * @param name - Cookie 的名称
 * @param value - Cookie 的值
 * @param options - Cookie 的选项
 */
export function setCookie(name: string, value: string, options: CookieOptions = {}): void {
    let cookieStr = `${encodeURIComponent(name)}=${encodeURIComponent(value)}`;

    if (options.maxAge !== undefined) {
        cookieStr += `; max-age=${options.maxAge}`;
    }

    if (options.expires) {
        cookieStr += `; expires=${options.expires}`;
    }

    cookieStr += `; path=${options.path || "/"}`;

    if (options.secure) {
        cookieStr += "; Secure";
    }

    if (options.sameSite) {
        cookieStr += `; SameSite=${options.sameSite}`;
    }

    document.cookie = cookieStr;
}

/**
 * 删除 Cookie
 * @param name - Cookie 的名称
 * @param options - Cookie 的选项
 */
export function deleteCookie(name: string, options: Partial<Pick<CookieOptions, "path">> = {}): void {
    setCookie(name, "", {
        path: options.path || "/",
        expires: "Thu, 01 Jan 1970 00:00:00 GMT", // 设置过期时间为过去
    });
}

/**
 * 获取指定名称的 Cookie 值
 * @param name - Cookie 的名称
 * @returns 对应 Cookie 的值，如果不存在则返回 null
 */
export function getCookie(name: string): string | null {
    const cookies = document.cookie.split("; "); // 分割所有 Cookie
    for (const cookie of cookies) {
        const [key, value] = cookie.split("="); // 分割键值对
        if (decodeURIComponent(key) === name) {
            return decodeURIComponent(value); // 返回解码后的值
        }
    }
    return null; // 未找到指定名称的 Cookie
}