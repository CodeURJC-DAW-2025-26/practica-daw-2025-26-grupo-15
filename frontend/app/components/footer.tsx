import { Link } from "react-router";
import { useUserStore } from "~/stores/user-store";
import { Container, Row, Col, Nav } from "react-bootstrap";

export function Footer() {
    const user = useUserStore((state) => state.user);
    const logged = user !== null;

    return (
        <footer className="site-footer py-4 mt-0">
            <Container>
                <Row className="align-items-center gy-3">
                    <Col xs={12} md={4}>
                        <div className="d-flex align-items-center gap-2 justify-content-center justify-content-md-start">
                            <Link to="/" className="brand-mark-link">
                                <img 
                                    src="/assets/DSGram_LOGO.png" 
                                    alt="DSGram logo" 
                                    className="brand-mark" 
                                />
                            </Link>
                            <span className="fw-bold text-white">DSGram</span>
                        </div>
                    </Col>
                    <Col xs={12} md={4}>
                        <Nav className="justify-content-center gap-3">
                            <Nav.Item>
                                <Nav.Link as={Link} to="/" className="footer-link p-0">
                                    Home
                                </Nav.Link>
                            </Nav.Item>
                            {logged && (
                                <Nav.Item>
                                    <Nav.Link as={Link} to={`/users/${user.id}`} className="footer-link p-0">
                                        Profile
                                    </Nav.Link>
                                </Nav.Item>
                            )}
                        </Nav>
                    </Col>
                    <Col xs={12} md={4} className="text-center text-md-end">
                        <small className="text-secondary">&copy; 2026 DSGram</small>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
}