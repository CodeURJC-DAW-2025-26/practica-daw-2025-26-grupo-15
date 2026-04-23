import { Footer } from "../components/footer";
import { Outlet, useNavigate, useNavigation } from "react-router";
import { useEffect } from "react";
import { useUserStore } from "~/stores/user-store";

export default function Home() {
  
  let { user, loadLoggedUser } = useUserStore();

  useEffect(() => {
    if (!user) {
      loadLoggedUser();
    }
  }, [user, loadLoggedUser]);
  
  const navigation = useNavigation();
  const isLoading = navigation.state === "loading";
  
  return (
    <>
     {isLoading && (
        <div className="page-spinner-overlay">
          <div className="dot-spinner" />
        </div>
      )}
      <Outlet />
      <Footer />
    </>
  );
}
