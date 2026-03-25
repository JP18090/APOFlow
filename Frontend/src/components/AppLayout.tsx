import type { ReactNode } from 'react';
import { AppSidebar } from '@/components/AppSidebar';
import { MackenzieLogo } from '@/components/MackenzieLogo';
import { SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar';
import { useAuth } from '@/contexts/AuthContext';

export default function AppLayout({ children }: { children: ReactNode }) {
  const { user } = useAuth();

  if (!user) {
    return null;
  }

  return (
    <SidebarProvider>
      <div className="flex min-h-screen w-full">
        <AppSidebar />
        <div className="flex min-w-0 flex-1 flex-col">
          <header className="flex h-14 shrink-0 items-center gap-3 border-b bg-card px-4">
            <SidebarTrigger />
            <h2 className="text-sm font-display font-semibold text-foreground">Sistema APOFlow</h2>
            <div className="ml-auto">
              <MackenzieLogo />
            </div>
          </header>
          <main className="flex-1 overflow-auto p-4 md:p-6">{children}</main>
        </div>
      </div>
    </SidebarProvider>
  );
}