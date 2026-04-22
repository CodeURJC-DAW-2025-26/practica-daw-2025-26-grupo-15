import { getUser, removeFollower, unFollowUser } from "~/services/user-service";
import { Button, Form } from "react-bootstrap";
import type { Route } from "./+types/followers-following";
import { useUserStore } from "~/stores/user-store";
import { InlineActionError } from "~/components/inline-action-error";
import { Link, useNavigate } from "react-router";
import { useActionState, useEffect, useRef } from "react";


export async function clientLoader({ request, params }: Route.ClientLoaderArgs) {
    const url = new URL(request.url);
    const userId = url.searchParams.get("userId");

    

    const type = params.type; 

    if (!userId) throw new Error("No userId provided");

    const userToShow = await getUser(Number(userId));
    
    return { userToShow, type };

}


export default function FollowingFollowers({loaderData}: Route.ComponentProps) {
    let { user } = useUserStore();
    const isOwnProfile = user && user.id === loaderData.userToShow.id;

    const navigate = useNavigate();
    const chartCanvasRef = useRef<HTMLCanvasElement | null>(null);

    const {userToShow, type} = loaderData;
    const followersPage = type === "followers";
    const listToShow = followersPage ? userToShow.followers : userToShow.following;

    useEffect(() => {
        const canvas = chartCanvasRef.current;
        if (!canvas || typeof window === "undefined") return;

        let chartInstance: { destroy: () => void } | null = null;
        let cancelled = false;

        const renderChart = () => {
            const chartCtor = (window as typeof window & {
                Chart?: new (ctx: CanvasRenderingContext2D, config: unknown) => { destroy: () => void };
            }).Chart;

            if (!chartCtor || cancelled) return;

            const followers = userToShow.followers.length;
            const following = userToShow.following.length;
            const maxValue = Math.max(followers, following);
            const context = canvas.getContext("2d");

            if (!context) return;

            chartInstance = new chartCtor(context, {
                type: "bar",
                data: {
                    labels: ["Followers", "Following"],
                    datasets: [
                        {
                            label: "Comparison",
                            data: [followers, following],
                            backgroundColor: ["#b8cdf2", "#b6f2e1"],
                            borderColor: ["#b8cdf2", "#b6f2e1"],
                            borderWidth: 1,
                        },
                    ],
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            suggestedMax: maxValue * 1.2,
                            ticks: {
                                precision: 0,
                            },
                        },
                    },
                    plugins: {
                        legend: {
                            display: false,
                        },
                    },
                },
            });
        };

        if ((window as typeof window & { Chart?: unknown }).Chart) {
            renderChart();
        } else {
            const script = document.createElement("script");
            script.src = "https://cdn.jsdelivr.net/npm/chart.js";
            script.async = true;
            script.dataset.chartjsCdn = "true";
            script.onload = () => renderChart();
            document.head.appendChild(script);
        }

        return () => {
            cancelled = true;
            chartInstance?.destroy();
        };
    }, [userToShow.followers.length, userToShow.following.length]);


    async function unFollowAction(_prevState:{errorUnfollow:string | null}, formData: FormData){
        const targetId = formData.get("targetId") as string;
        try{
          await unFollowUser(targetId);
          navigate("."+`?userId=${userToShow.id}`,{ replace: true });
          return {errorUnfollow:null};
        }catch(error){
          return {errorUnfollow:"Failed to unfollow user"}
        }
    
      }

    const[{errorUnfollow},formUnfollowAction,isPendingUnfollow] = useActionState(unFollowAction,{errorUnfollow:null});
    
    async function removeFollowerAction(_prevState:{errorRemoveFollower:string | null}, formData: FormData){
        const toRemoveId = formData.get("requesterId") as string;
        try{
            await removeFollower(toRemoveId);
            navigate("."+`?userId=${userToShow.id}`,{ replace: true });
            return {errorRemoveFollower:null};
        }catch(error){
            return {errorRemoveFollower:"Failed to remove follower"}
        }
    }
    const [{errorRemoveFollower}, formRemoveFollowerAction, isPendingRemoveFollower] = useActionState(removeFollowerAction, {errorRemoveFollower:null});


    return (
        <>
            <main className="page page--feed">
                <div className="brand">
                    <Link to="/" className="brand-mark-link">
                        <img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" />
                    </Link>
                    <Link to="/">
                        <span className="brand-title">DSGram</span>
                    </Link>
                </div>

                <section className="app-shell feed">
                    <div className="container">
                        <div className="row">
                            <div className="content col-12 mx-0 g-0">
                                {/* BARRA SUPERIOR: Títulos dinámicos */}
                                <div className="topbar d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-0 mt-3">
                                    <div>
                                        <h2 className="section-title">
                                            {followersPage ? "Followers" : "Following"}
                                        </h2>
                                        <p className="muted">
                                            {followersPage 
                                                ? (isOwnProfile ? "Users that follow you" : `Users that follow ${userToShow.name}`)
                                                : (isOwnProfile ? "Users you are following" : `Users that ${userToShow.name} is following`)
                                            }
                                        </p>
                                    </div>
                                    <Link className="btn ghost" to={`/users/${userToShow.id}`}>Back</Link>
                                </div>

                                <div className="row align-items-start justify-content-between">
                                    {/* COLUMNA IZQUIERDA: Lista de usuarios */}
                                    <div className="col-md-8 col-12 followers-card mx-0 mt-5">
                                        <h3>{followersPage ? "Followers" : "Following"}</h3>
                                        <div className="followers-list">
                                            {userToShow && listToShow.length > 0 ? (
                                                listToShow.map((item: typeof userToShow) => (
                                                    <div key={item.id} className="followers-item">
                                                        <div className="followers-left">
                                                            <div className="followers-avatar avatar--img">
                                                                {item.photo ? (
                                                                    <img src={`/api/v1/images/${item.photo.id}/media`} alt={item.name} className="avatar-image-cover" />
                                                                ) : (
                                                                    <span>{item.nameInitial}</span>
                                                                )}
                                                            </div>
                                                            <span>{item.name}</span>
                                                        </div>
                                                        <div className="d-flex gap-2">
                                                            <Link className="btn secondary followers-action p-0" to={`/users/${item.id}`}>View</Link>
                                                            
                                                            {isOwnProfile && (
                                                                followersPage ? (<>
                                                                    <Form method="post" action={formRemoveFollowerAction}>
                                                                        <Form.Control type="hidden" name="requesterId" disabled={isPendingRemoveFollower} value={item!.id} />
                                                                        <Button className="btn followers-action btn-danger-action" type="submit">Remove</Button>
                                                                    </Form>
                                                                    <InlineActionError message={errorRemoveFollower} />
                                                                    </>
                                                                ) : (<>
                                                                    <Form action={formUnfollowAction}>
                                                                        <Form.Control type="hidden" name="requesterId" disabled={isPendingUnfollow} value={user!.id} />
                                                                        <Form.Control type="hidden" name="targetId" disabled={isPendingUnfollow} value={item!.id} />
                                                                        <Button className="btn followers-action btn-danger-action" type="submit">Unfollow</Button>
                                                                    </Form>
                                                                    <InlineActionError message={errorUnfollow} />
                                                                    </>
                                                                )
                                                            )}
                                                        </div>
                                                    </div>
                                                ))
                                            ) : (
                                                <p>
                                                    {followersPage 
                                                        ? (isOwnProfile ? "You don't have any followers yet." : "This user doesn't have any followers yet.")
                                                        : (isOwnProfile ? "You're not following anyone yet." : "This user isn't following anyone yet.")
                                                    }
                                                </p>
                                            )}
                                        </div>
                                    </div>

                                    {/* COLUMNA DERECHA: Gráfico (Canvas) */}
                                    {/* TODO: El gráfico se mostrará más adelante    */}
                                    <div className="col-md-4 col-12 chart-container mx-0 mt-5">
                                        <canvas 
                                            ref={chartCanvasRef}
                                            id="comparisonChart" 
                                            data-num-followers={userToShow.followers.length} 
                                            data-num-following={userToShow.following.length}
                                        ></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
        </>
    );
}