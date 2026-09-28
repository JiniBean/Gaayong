export interface AuthUser {
  userNm: string
  name: string
  theme: string
}

export interface LoginRequest {
  userNm: string
  pwd: string
  rememberMe?: boolean
}

export interface SignupRequest {
  userNm: string
  name: string
  pwd: string
  email: string
}

export interface MessageResponse {
  message: string
}
