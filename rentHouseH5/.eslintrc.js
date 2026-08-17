/* eslint-env node */
require("@rushstack/eslint-patch/modern-module-resolution");

module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [
    "plugin:vue/vue3-essential",
    "eslint:recommended",
    "@vue/eslint-config-typescript",
    "@vue/eslint-config-prettier"
  ],
  parserOptions: {
    ecmaVersion: "latest",
    parser: "@typescript-eslint/parser",
    sourceType: "module",
    jsxPragma: "React",
    ecmaFeatures: {
      jsx: true
    }
  },
  parser: "vue-eslint-parser",
  rules: {
    "vue/multi-word-component-names": 0,
    "prettier/prettier": ["error", { endOfLine: "auto" }]
    // [
    //   "error",
    //   {
    //     ignores: ["index"] // 需要忽略的组件名
    //   }
    // ]
  },
  overrides: [
    {
      files: ["*.html"],
      processor: "vue/.vue"
    }
  ]
};
