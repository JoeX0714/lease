import { resultError, resultSuccess, getRequestToken } from './_utils'

// mock/user.ts

function createUserList() {
  return [
    {
      userId: 1,
      avatar:
        'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
      username: 'admin',
      password: '111111',
      desc: '平台管理员',
      roles: ['平台管理员'],
      buttons: ['cuser.detail'],
      routers: [
        {
          path: '/system',
          component: 'Layout',
          meta: {
            title: '系统管理',
            icon: 'Lock',
            isHide: true,
          },
          children: [],
        },
      ],
      token:
        'eyJhbGciOiJIUzM4NCIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScnTx9fSLDw12DVLSUUqtKFCyMjQ3MrA0NjcxNdFRKi1OLfJMAYoZQth5ibmpQE1VGYl56cWJeUq1AECB2H1HAAAA.rXn8vJrR-RswpsoPbL49GQhMG6GCC449HJfZpTzwW_tV6aIUMJeVGjof3a69ASBk',
    },
    {
      userId: 2,
      avatar:
        'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
      username: 'system',
      password: '111111',
      desc: '系统管理员',
      roles: ['系统管理员'],
      buttons: ['cuser.detail', 'cuser.user'],
      routers: [
        {
          path: '/system',
          component: 'Layout',
          meta: {
            title: '系统管理',
            icon: 'Lock',
            isHide: true,
          },
          children: [],
        },
      ],
      token: 'System Token',
    },
  ]
}

const mockApiList = [
  // 用户登录
  {
    url: '/admin/login',
    method: 'post',
    response: ({ body }: { body: any }) => {
      const { username, password } = body
      const checkUser = createUserList().find(
        (item) => item.username === username && item.password === password,
      )
      if (!checkUser) {
        return resultError('Incorrect username or password！')
      }
      const { token } = checkUser
      return resultSuccess(token)
    },
  },
  // 获取用户信息
  {
    url: '/admin/system/index/info',
    method: 'get',
    response: (request: any) => {
      const token = getRequestToken(request)
      const checkUser = createUserList().find((item) => item.token === token)
      if (!checkUser) {
        return resultError(
          'The corresponding user information was not obtained!',
        )
      }
      return resultSuccess(checkUser)
    },
  },
  // 一个失败的请求
  {
    url: '/error',
    method: 'get',
    response: () => {
      return {
        code: 1,
        message: '密码错误',
        data: null,
      }
    },
  },
]
export default mockApiList.map((item) => {
  return {
    ...item,
    url: `/mock${item.url}`,
  }
})
