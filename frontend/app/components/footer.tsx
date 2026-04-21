import { Link } from "react-router";
import { useUserStore } from "~/stores/user-store";

export function Footer(){
    const {user} = useUserStore();
    const logged = user !== null;
    return (
        <>
        <footer className="site-footer py-4 mt-0">
            <div className="container">
            <div className="row align-items-center gy-3">
                <div className="col-12 col-md-4">
                <div className="d-flex align-items-center gap-2 justify-content-center justify-content-md-start">
                    <a className="brand-mark-link"><img alt="DSGram logo" className="brand-mark" /></a>
                    <span className="fw-bold text-white">DSGram</span>
                </div>
                </div>
                <div className="col-12 col-md-4">
                <ul className="nav justify-content-center gap-3 list-unstyled d-flex mb-0">
                    <li><Link to="/" className="footer-link">Home</Link></li>
                    {logged && (
                        <li><Link to={`/users/${user.id}`} className="footer-link">Profile</Link></li>
                    )}
                </ul>
                </div>
                <div className="col-12 col-md-4 text-center text-md-end">
                <small className="text-secondary">&copy; 2026 DSGram</small>
                </div>
            </div>
            </div>
        </footer>
    </>
    )
}