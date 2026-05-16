import { createContext, useContext, useState, type ReactNode } from 'react'
import { type User, getUser, setUser as saveUser, clearUser, authAPI } from '../api'

interface AuthContextType {
  user: User | null
  login: (email: string, senha: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  login: async () => {},
  logout: async () => {},
})

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(getUser())

  async function login(email: string, senha: string) {
    const u = await authAPI.login(email, senha)
    saveUser(u!)
    setUser(u)
  }

  async function logout() {
    try { await authAPI.logout() } catch {}
    clearUser()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
