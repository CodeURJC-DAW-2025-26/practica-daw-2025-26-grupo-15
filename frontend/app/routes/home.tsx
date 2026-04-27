import { Footer } from "../components/footer";
import { ErrorBoundaryScreen } from "../components/error-boundary-screen";
import { Outlet, useNavigation } from "react-router";
import { useEffect } from "react";
import { useUserStore } from "~/stores/user-store";
import type { Route } from "./+types/home";

export default function Home() {
  
  const loadLoggedUser = useUserStore((state) => state.loadLoggedUser);

  useEffect(() => {
    loadLoggedUser();
  }, [loadLoggedUser]);
  
  const navigation = useNavigation();
  const isLoading = navigation.state === "loading";
  
  return (
    <div className="app-with-footer">
     {isLoading && (
        <div className="page-spinner-overlay">
          <div className="dot-spinner" />
        </div>
      )}
      <Outlet />
      <Footer />
    </div>
  );
  
}
export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  return <ErrorBoundaryScreen error={error} />;
}
