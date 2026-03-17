import {
  Archive,
  Bell,
  ClipboardList,
  FilePlus,
  GraduationCap,
  LayoutDashboard,
  LogOut,
  ShieldCheck,
  Vote,
} from 'lucide-react';
import { useLocation } from 'react-router-dom';
import { NavLink } from '@/components/NavLink';
import { Button } from '@/components/ui/button';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@/components/ui/sidebar';
import { useAuth } from '@/contexts/AuthContext';
import { Role } from '@/lib/mock-data';

interface NavItem {
  title: string;
  url: string;
  icon: React.ComponentType<{ className?: string }>;
}

const navByRole: Record<Role, NavItem[]> = {
  aluno: [
    { title: 'Painel', url: '/', icon: LayoutDashboard },
    { title: 'Nova APO', url: '/nova-apo', icon: FilePlus },
    { title: 'Minhas APOs', url: '/minhas-apos', icon: ClipboardList },
    { title: 'Notificações', url: '/notificacoes', icon: Bell },
  ],
  orientador: [
    { title: 'Painel', url: '/', icon: LayoutDashboard },
    { title: 'Pendências', url: '/pendencias', icon: ClipboardList },
    { title: 'Notificações', url: '/notificacoes', icon: Bell },
  ],
  comissao: [
    { title: 'Painel', url: '/', icon: LayoutDashboard },
    { title: 'Itens p/ Votação', url: '/votacao', icon: Vote },
    { title: 'Notificações', url: '/notificacoes', icon: Bell },
  ],
  coordenacao: [
    { title: 'Painel', url: '/', icon: LayoutDashboard },
    { title: 'Avaliação Final', url: '/avaliacao-final', icon: ShieldCheck },
    { title: 'Notificações', url: '/notificacoes', icon: Bell },
  ],
  secretaria: [
    { title: 'Painel', url: '/', icon: LayoutDashboard },
    { title: 'Fila de Lançamento', url: '/lancamento', icon: Archive },
    { title: 'Notificações', url: '/notificacoes', icon: Bell },
  ],
};

const roleLabels: Record<Role, string> = {
  aluno: 'Aluno',
  orientador: 'Orientador',
  comissao: 'Comissão',
  coordenacao: 'Coordenação',
  secretaria: 'Secretaria',
};

export function AppSidebar() {
  const { state } = useSidebar();
  const collapsed = state === 'collapsed';
  const { user, logout } = useAuth();
  const location = useLocation();

  if (!user) {
    return null;
  }

  const items = navByRole[user.papel];

  return (
    <Sidebar collapsible="icon">
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>
            <div className="flex items-center gap-2">
              <GraduationCap className="h-4 w-4" />
              {!collapsed && <span className="font-display font-semibold">APOFlow</span>}
            </div>
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {items.map((item) => (
                <SidebarMenuItem key={item.url}>
                  <SidebarMenuButton asChild>
                    <NavLink
                      to={item.url}
                      end={item.url === '/'}
                      className="hover:bg-sidebar-accent/50"
                      activeClassName="bg-sidebar-accent text-sidebar-primary font-medium"
                    >
                      <item.icon className="mr-2 h-4 w-4" />
                      {!collapsed && <span>{item.title}</span>}
                    </NavLink>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter className="p-3">
        {!collapsed && (
          <div className="mb-2 px-2">
            <p className="font-body text-xs text-sidebar-foreground/60">Logado como</p>
            <p className="truncate font-display text-sm font-semibold text-sidebar-foreground">{user.nome}</p>
            <p className="font-body text-xs text-sidebar-primary">{roleLabels[user.papel]}</p>
          </div>
        )}
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-start text-sidebar-foreground/60 hover:bg-sidebar-accent hover:text-sidebar-foreground"
          onClick={logout}
          title={`Sair da sessão atual (${location.pathname})`}
        >
          <LogOut className="mr-2 h-4 w-4" />
          {!collapsed && 'Sair'}
        </Button>
      </SidebarFooter>
    </Sidebar>
  );
}
