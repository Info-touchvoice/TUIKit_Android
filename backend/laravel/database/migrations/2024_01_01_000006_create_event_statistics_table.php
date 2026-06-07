<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('event_statistics', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('event_id');
            $table->integer('total_participants')->default(0);
            $table->integer('total_active_participants')->default(0);
            $table->integer('total_completed_participants')->default(0);
            $table->decimal('total_recharge_amount', 18, 2)->default(0);
            $table->decimal('total_spending_amount', 18, 2)->default(0);
            $table->decimal('total_rewards_distributed', 18, 2)->default(0);
            $table->bigInteger('total_coins_distributed')->default(0);
            $table->bigInteger('total_diamonds_distributed')->default(0);
            $table->integer('total_vip_days_distributed')->default(0);
            $table->integer('total_frames_distributed')->default(0);
            $table->integer('total_badges_distributed')->default(0);
            $table->integer('total_gifts_distributed')->default(0);
            $table->dateTime('last_updated')->useCurrent();

            $table->unique('event_id');
            $table->foreign('event_id')->references('id')->on('events')->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('event_statistics');
    }
};
