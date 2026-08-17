import { showDialog } from "vant";
import router from "@/router";

export const useToLoginPage = () => {
  /**
   * @description: 跳转到登录页
   * @param path 跳转后的路径
   */
  const showToLoginPageDialog = (path: string = "/login") => {
    showDialog({
      message: "登录失效，请重新登录"
    }).then(() => {
      const redirect = router.options.history.location
        ? `?redirect=${encodeURIComponent(router.options.history.location)}`
        : "";
      router.replace(`${path}${redirect}`);
    });
  };
  return {
    showToLoginPageDialog
  };
};
