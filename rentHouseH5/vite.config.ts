import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
import Components from "unplugin-vue-components/vite";
import { VantResolver } from "unplugin-vue-components/resolvers";
import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import path from "path";
import mockDevServerPlugin from "vite-plugin-mock-dev-server";
import vueSetupExtend from "vite-plugin-vue-setup-extend";
import viteCompression from "vite-plugin-compression";
import { createHtmlPlugin } from "vite-plugin-html";

// 当前工作目录路径
const root: string = process.cwd();

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 环境变量
  const env = loadEnv(mode, root, "");
  return {
    base: "/",
    plugins: [
      vue(),
      vueJsx(),
      mockDevServerPlugin(),
      // vant 组件自动按需引入
      Components({
        resolvers: [VantResolver()]
      }),
      // svg icon
      createSvgIconsPlugin({
        // 指定图标文件夹
        iconDirs: [path.resolve(root, "src/icons/svg")],
        // 指定 symbolId 格式
        symbolId: "icon-[dir]-[name]",
        //svgo额外配置，具体配置参考https://github.com/svg/svgo
        svgoOptions: {
          plugins: [
            // 去除所有svg的"class", "data-name", "fill", "stroke"属性
            {
              name: "removeAttrs",
              params: { attrs: ["class", "data-name", "fill", "stroke"] }
            }
          ]
        }
      }),
      // 允许 setup 语法糖上添加组件名属性
      vueSetupExtend(),
      viteCompression(),
      // 注入模板数据
      createHtmlPlugin({
        inject: {
          data: {
            ENABLE_ERUDA: env.VITE_ENABLE_ERUDA || "false"
          }
        }
      })
    ],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url))
      }
    },
    server: {
      host: "localhost",
      proxy: {
        "/app": {
          target: env.VITE_APP_BASE_URL,
          changeOrigin: true
        }
      }
    },
    build: {
      rollupOptions: {
        output: {
          chunkFileNames: "static/js/[name]-[hash].js",
          entryFileNames: "static/js/[name]-[hash].js",
          assetFileNames: "static/[ext]/[name]-[hash].[ext]"
        }
      }
    }
  };
});
